package cloud.luigi99.blog.content.application.post.service.command

import cloud.luigi99.blog.content.application.post.port.`in`.command.DeletePostUseCase
import cloud.luigi99.blog.content.application.post.port.out.PostRepository
import cloud.luigi99.blog.content.domain.post.exception.PostNotFoundException
import cloud.luigi99.blog.content.domain.post.exception.UnauthorizedPostAccessException
import cloud.luigi99.blog.content.domain.post.vo.PostId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

private val log = KotlinLogging.logger {}

/**
 * Post 삭제 유스케이스 구현체
 *
 * Post를 조회하고 권한을 검증한 후 삭제합니다.
 */
@Service
class DeletePostService(private val postRepository: PostRepository) : DeletePostUseCase {
    @Transactional
    override fun execute(command: DeletePostUseCase.Command) {
        log.info { "Deleting post: ${command.postId} by member: ${command.memberId}" }

        val postId = PostId(UUID.fromString(command.postId))
        val memberId = MemberId.from(command.memberId)

        val post =
            postRepository.findById(postId)
                ?: throw PostNotFoundException("Post ID ${command.postId}를 찾을 수 없습니다")

        if (!post.isOwner(memberId)) {
            throw UnauthorizedPostAccessException("게시글 ${command.postId}에 대한 삭제 권한이 없습니다")
        }

        post.delete()

        postRepository.deleteById(postId)

        log.info { "Successfully deleted post: $postId" }
    }
}
