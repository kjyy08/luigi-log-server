package cloud.luigi99.blog.content.post.application.service.query

import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostBySlugUseCase
import cloud.luigi99.blog.content.post.application.port.out.MemberClient
import cloud.luigi99.blog.content.post.application.port.out.PostRepository
import cloud.luigi99.blog.content.post.application.port.out.PostViewCountDeduplicationPort
import cloud.luigi99.blog.content.post.domain.exception.PostNotFoundException
import cloud.luigi99.blog.content.post.domain.model.Post
import cloud.luigi99.blog.content.post.domain.vo.PostId
import cloud.luigi99.blog.content.post.domain.vo.PostStatus
import cloud.luigi99.blog.content.post.domain.vo.Slug
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * Username과 Slug로 Post 조회 유스케이스 구현체
 *
 * 사용자 이름과 URL slug를 사용하여 Post를 조회합니다.
 */
@Service
class GetPostBySlugService(
    private val postRepository: PostRepository,
    private val memberClient: MemberClient,
    private val postViewCountDeduplicationPort: PostViewCountDeduplicationPort,
) : GetPostBySlugUseCase {
    @Transactional
    override fun execute(query: GetPostBySlugUseCase.Query): GetPostBySlugUseCase.Response {
        log.info { "Getting post by username: ${query.username}, slug: ${query.slug}" }

        val slug = Slug(query.slug)
        val post =
            postRepository.findByUsernameAndSlug(query.username, slug)
                ?: throw PostNotFoundException(
                    "사용자 '${query.username}'의 Slug '${query.slug}'에 해당하는 Post를 찾을 수 없습니다",
                )
        if (post.status == PostStatus.PUBLISHED && isUniqueView(post.entityId, query.visitorKey)) {
            val updatedRows = postRepository.incrementViewCount(post.entityId)
            if (updatedRows > 0) {
                post.incrementViewCount()
            }
        }
        val commentCount = postRepository.countCommentsByPostIds(listOf(post.entityId))[post.entityId] ?: 0
        val previousPost =
            if (post.status == PostStatus.PUBLISHED) {
                postRepository.findPreviousPublishedPost(post)?.toAdjacentPostInfo()
            } else {
                null
            }
        val nextPost =
            if (post.status == PostStatus.PUBLISHED) {
                postRepository.findNextPublishedPost(post)?.toAdjacentPostInfo()
            } else {
                null
            }

        val author =
            memberClient.getAuthor(
                post.memberId.value
                    .toString(),
            )

        return GetPostBySlugUseCase.Response(
            postId =
                post.entityId.value
                    .toString(),
            author =
                GetPostBySlugUseCase.AuthorInfo(
                    memberId = author.memberId,
                    nickname = author.nickname,
                    profileImageUrl = author.profileImageUrl,
                    username = author.username,
                ),
            title = post.title.value,
            slug = post.slug.value,
            body = post.body.value,
            type = post.type.name,
            status = post.status.name,
            tags = post.tags,
            viewCount = post.viewCount,
            commentCount = commentCount,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt,
            previousPost = previousPost,
            nextPost = nextPost,
        )
    }

    private fun Post.toAdjacentPostInfo(): GetPostBySlugUseCase.AdjacentPostInfo =
        GetPostBySlugUseCase.AdjacentPostInfo(
            postId =
                entityId.value
                    .toString(),
            title = title.value,
            slug = slug.value,
            createdAt = createdAt,
        )

    private fun isUniqueView(postId: PostId, visitorKey: String): Boolean =
        runCatching { postViewCountDeduplicationPort.isUniqueView(postId, visitorKey) }
            .onFailure { e -> log.warn(e) { "Failed to deduplicate post view count. postId=$postId" } }
            .getOrDefault(false)
}
