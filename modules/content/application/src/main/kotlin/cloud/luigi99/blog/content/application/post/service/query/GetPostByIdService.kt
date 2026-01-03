package cloud.luigi99.blog.content.application.post.service.query

import cloud.luigi99.blog.content.application.post.port.`in`.query.GetPostByIdUseCase
import cloud.luigi99.blog.content.application.post.port.out.MemberClient
import cloud.luigi99.blog.content.application.post.port.out.PostRepository
import cloud.luigi99.blog.content.domain.post.exception.PostNotFoundException
import cloud.luigi99.blog.content.domain.post.vo.PostId
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
class GetPostByIdService(private val postRepository: PostRepository, private val memberClient: MemberClient) :
    GetPostByIdUseCase {
    @Transactional(readOnly = true)
    override fun execute(query: GetPostByIdUseCase.Query): GetPostByIdUseCase.Response {
        log.info { "Getting post by id: ${query.postId}" }

        val postId = PostId(UUID.fromString(query.postId))
        val post =
            postRepository.findById(postId)
                ?: throw PostNotFoundException("Post ID ${query.postId}를 찾을 수 없습니다")

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
                ),
            title = post.title.value,
            slug = post.slug.value,
            body = post.body.value,
            type = post.type.name,
            status = post.status.name,
            tags = post.tags,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt,
        )
    }
}
