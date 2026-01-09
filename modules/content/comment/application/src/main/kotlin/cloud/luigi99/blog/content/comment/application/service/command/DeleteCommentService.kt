package cloud.luigi99.blog.content.comment.application.service.command

import cloud.luigi99.blog.content.comment.application.port.`in`.command.DeleteCommentUseCase
import cloud.luigi99.blog.content.comment.application.port.out.CommentRepository
import cloud.luigi99.blog.content.comment.domain.exception.CommentNotFoundException
import cloud.luigi99.blog.content.comment.domain.vo.CommentId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 댓글 삭제 서비스
 *
 * 작성한 댓글을 삭제하는 기능을 제공합니다.
 */
@Service
class DeleteCommentService(private val commentRepository: CommentRepository) : DeleteCommentUseCase {
    /**
     * 댓글을 삭제합니다.
     */
    @Transactional
    override fun execute(command: DeleteCommentUseCase.Command) {
        log.info { "Deleting comment: ${command.commentId}" }

        val commentId = CommentId.from(command.commentId)
        val authorId = MemberId.from(command.authorId)

        val comment =
            commentRepository.findById(commentId)
                ?: throw CommentNotFoundException("Comment not found: $commentId")

        comment.delete(authorId)
        commentRepository.deleteById(commentId)

        log.info { "Successfully deleted comment: $commentId" }
    }
}
