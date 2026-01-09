package cloud.luigi99.blog.content.comment.application.service.command

import cloud.luigi99.blog.content.comment.application.port.`in`.command.UpdateCommentUseCase
import cloud.luigi99.blog.content.comment.application.port.out.CommentRepository
import cloud.luigi99.blog.content.comment.application.port.out.MemberClient
import cloud.luigi99.blog.content.comment.domain.exception.CommentNotFoundException
import cloud.luigi99.blog.content.comment.domain.vo.CommentContent
import cloud.luigi99.blog.content.comment.domain.vo.CommentId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 댓글 수정 서비스
 *
 * 작성한 댓글의 내용을 수정하는 기능을 제공합니다.
 */
@Service
class UpdateCommentService(private val commentRepository: CommentRepository, private val memberClient: MemberClient) :
    UpdateCommentUseCase {
    /**
     * 댓글을 수정합니다.
     */
    @Transactional
    override fun execute(command: UpdateCommentUseCase.Command): UpdateCommentUseCase.Response {
        log.info { "Updating comment: ${command.commentId}" }

        val commentId = CommentId.from(command.commentId)
        val authorId = MemberId.from(command.authorId)
        val newContent = CommentContent(command.content)

        val comment =
            commentRepository.findById(commentId)
                ?: throw CommentNotFoundException("Comment not found: $commentId")

        val updatedComment = comment.updateContent(newContent, authorId)
        val savedComment = commentRepository.save(updatedComment)

        log.info { "Successfully updated comment: ${savedComment.entityId}" }

        val author = memberClient.getAuthor(command.authorId)

        return UpdateCommentUseCase.Response(
            commentId =
                savedComment.entityId.value
                    .toString(),
            author =
                UpdateCommentUseCase.AuthorInfo(
                    memberId = author.memberId,
                    nickname = author.nickname,
                    profileImageUrl = author.profileImageUrl,
                    username = author.username,
                ),
            content = savedComment.content.value,
            updatedAt = savedComment.updatedAt.toString(),
        )
    }
}
