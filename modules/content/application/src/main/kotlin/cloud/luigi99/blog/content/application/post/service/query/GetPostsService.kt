package cloud.luigi99.blog.content.application.post.service.query

import cloud.luigi99.blog.content.application.post.port.`in`.query.GetPostsUseCase
import cloud.luigi99.blog.content.application.post.port.out.MemberClient
import cloud.luigi99.blog.content.application.post.port.out.PostRepository
import cloud.luigi99.blog.content.domain.post.vo.ContentType
import cloud.luigi99.blog.content.domain.post.vo.PostStatus
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * Post 목록 조회 유스케이스 구현체
 *
 * 필터링 조건에 따라 Post 목록을 조회합니다.
 */
@Service
class GetPostsService(private val postRepository: PostRepository, private val memberClient: MemberClient) :
    GetPostsUseCase {
    @Transactional(readOnly = true)
    override fun execute(query: GetPostsUseCase.Query): GetPostsUseCase.Response {
        log.info { "Listing posts with filters - status: ${query.status}, type: ${query.type}" }

        val posts =
            when {
                query.status != null && query.type != null -> {
                    val status = PostStatus.valueOf(query.status)
                    val type = ContentType.valueOf(query.type)
                    postRepository
                        .findAllByStatus(status)
                        .filter { it.type == type }
                }

                query.status != null -> {
                    val status = PostStatus.valueOf(query.status)
                    postRepository.findAllByStatus(status)
                }

                query.type != null -> {
                    val type = ContentType.valueOf(query.type)
                    postRepository.findAllByContentType(type)
                }

                else -> {
                    postRepository.findAll()
                }
            }

        val memberIds =
            posts
                .map {
                    it.memberId.value
                        .toString()
                }.distinct()
        val authors = memberClient.getAuthors(memberIds)

        val summaries =
            posts.map { post ->
                val memberIdStr =
                    post.memberId.value
                        .toString()
                val author = authors[memberIdStr] ?: MemberClient.Author(memberIdStr, "Unknown", null)

                GetPostsUseCase.PostSummary(
                    postId =
                        post.entityId.value
                            .toString(),
                    author =
                        GetPostsUseCase.AuthorInfo(
                            memberId = author.memberId,
                            nickname = author.nickname,
                            profileImageUrl = author.profileImageUrl,
                        ),
                    title = post.title.value,
                    slug = post.slug.value,
                    type = post.type.name,
                    status = post.status.name,
                    tags = post.tags,
                    createdAt = post.createdAt,
                )
            }

        log.info { "Found ${summaries.size} posts" }

        return GetPostsUseCase.Response(posts = summaries)
    }
}
