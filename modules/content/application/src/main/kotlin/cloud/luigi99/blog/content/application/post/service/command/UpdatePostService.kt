package cloud.luigi99.blog.content.application.post.service.command

import cloud.luigi99.blog.content.application.post.port.`in`.command.UpdatePostUseCase
import cloud.luigi99.blog.content.application.post.port.out.MemberClient
import cloud.luigi99.blog.content.application.post.port.out.PostRepository
import cloud.luigi99.blog.content.domain.post.exception.PostNotFoundException
import cloud.luigi99.blog.content.domain.post.exception.UnauthorizedPostAccessException
import cloud.luigi99.blog.content.domain.post.vo.Body
import cloud.luigi99.blog.content.domain.post.vo.PostId
import cloud.luigi99.blog.content.domain.post.vo.Title
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

private val log = KotlinLogging.logger {}

/**
 * Post 수정 유스케이스 구현체
 *
 * Post의 제목, 본문, 상태를 선택적으로 수정합니다.
 */
@Service
class UpdatePostService(private val postRepository: PostRepository, private val memberClient: MemberClient) :
    UpdatePostUseCase {
    @Transactional
    override fun execute(command: UpdatePostUseCase.Command): UpdatePostUseCase.Response {
        log.info {
            "Updating post: ${command.postId} by member: ${command.memberId} (title=${command.title != null}, body=${command.body != null}, status=${command.status})"
        }

        val memberId = MemberId.from(command.memberId)
        val postId = PostId(UUID.fromString(command.postId))
        var post =
            postRepository.findById(postId)
                ?: throw PostNotFoundException("Post ID ${command.postId}를 찾을 수 없습니다")

        // 권한 검증
        if (!post.isOwner(memberId)) {
            throw UnauthorizedPostAccessException("게시글 ${command.postId}에 대한 수정 권한이 없습니다")
        }

        // 1. 제목/본문 수정 (둘 중 하나라도 있으면)
        if (command.title != null || command.body != null) {
            val newTitle = command.title?.let { Title(it) } ?: post.title
            val newBody = command.body?.let { Body(it) } ?: post.body
            post = post.update(newTitle, newBody)
        }

        // 2. 상태 변경 (status가 있으면)
        if (command.status != null) {
            post =
                when (command.status.uppercase()) {
                    "PUBLISHED" -> post.publish()

                    "ARCHIVED" -> post.archive()

                    "DRAFT" -> post

                    // DRAFT는 초기 상태이므로 변경 불필요 (필요하면 도메인에 toDraft() 메서드 추가)
                    else -> throw IllegalArgumentException("Invalid status: ${command.status}")
                }
        }

        val savedPost = postRepository.save(post)

        log.info { "Successfully updated post: ${savedPost.entityId}, status=${savedPost.status}" }

        val author =
            memberClient.getAuthor(
                savedPost.memberId.value
                    .toString(),
            )

        return UpdatePostUseCase.Response(
            postId =
                savedPost.entityId.value
                    .toString(),
            author =
                UpdatePostUseCase.AuthorInfo(
                    memberId = author.memberId,
                    nickname = author.nickname,
                    profileImageUrl = author.profileImageUrl,
                ),
            title = savedPost.title.value,
            slug = savedPost.slug.value,
            body = savedPost.body.value,
            type = savedPost.type.name,
            status = savedPost.status.name,
            tags = savedPost.tags,
            createdAt = savedPost.createdAt,
            updatedAt = savedPost.updatedAt,
        )
    }
}
