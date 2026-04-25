package cloud.luigi99.blog.content.post.application.service.query

import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostByIdUseCase
import cloud.luigi99.blog.content.post.application.port.out.MemberClient
import cloud.luigi99.blog.content.post.application.port.out.PostRepository
import cloud.luigi99.blog.content.post.application.port.out.PostViewCountDeduplicationPort
import cloud.luigi99.blog.content.post.domain.exception.PostNotFoundException
import cloud.luigi99.blog.content.post.domain.vo.PostId
import cloud.luigi99.blog.content.post.domain.vo.PostStatus
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

private val log = KotlinLogging.logger {}

/**
 * ID로 Post 조회 유스케이스 구현체
 *
 * Post ID를 사용하여 Post를 조회합니다.
 */
@Service
class GetPostByIdService(
    private val postRepository: PostRepository,
    private val memberClient: MemberClient,
    private val postViewCountDeduplicationPort: PostViewCountDeduplicationPort,
) : GetPostByIdUseCase {
    @Transactional
    override fun execute(query: GetPostByIdUseCase.Query): GetPostByIdUseCase.Response {
        log.info { "Getting post by id: ${query.postId}" }

        val postId = PostId(UUID.fromString(query.postId))
        val post =
            postRepository.findById(postId)
                ?: throw PostNotFoundException("Post ID ${query.postId}를 찾을 수 없습니다")
        if (post.status == PostStatus.PUBLISHED && isUniqueView(post.entityId, query.visitorKey)) {
            val updatedRows = postRepository.incrementViewCount(post.entityId)
            if (updatedRows > 0) {
                post.incrementViewCount()
            }
        }
        val commentCount = postRepository.countCommentsByPostIds(listOf(post.entityId))[post.entityId] ?: 0

        val author =
            memberClient.getAuthor(
                post.memberId.value
                    .toString(),
            )

        return GetPostByIdUseCase.Response(
            postId =
                post.entityId.value
                    .toString(),
            author =
                GetPostByIdUseCase.AuthorInfo(
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
        )
    }

    private fun isUniqueView(postId: PostId, visitorKey: String): Boolean =
        runCatching { postViewCountDeduplicationPort.isUniqueView(postId, visitorKey) }
            .onFailure { e -> log.warn(e) { "Failed to deduplicate post view count. postId=$postId" } }
            .getOrDefault(false)
}
