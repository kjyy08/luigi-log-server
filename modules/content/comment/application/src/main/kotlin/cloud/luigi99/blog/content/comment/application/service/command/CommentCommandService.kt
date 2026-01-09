package cloud.luigi99.blog.content.comment.application.service.command

import cloud.luigi99.blog.content.comment.application.port.`in`.command.CommentCommandFacade
import cloud.luigi99.blog.content.comment.application.port.`in`.command.CreateCommentUseCase
import cloud.luigi99.blog.content.comment.application.port.`in`.command.DeleteCommentUseCase
import cloud.luigi99.blog.content.comment.application.port.`in`.command.UpdateCommentUseCase
import org.springframework.stereotype.Service

/**
 * 댓글 명령 서비스
 *
 * 댓글 생성, 수정, 삭제와 관련된 유스케이스들을 제공하는 Facade 구현체입니다.
 */
@Service
class CommentCommandService(
    private val createCommentUseCase: CreateCommentUseCase,
    private val updateCommentUseCase: UpdateCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
) : CommentCommandFacade {
    override fun createComment(): CreateCommentUseCase = createCommentUseCase

    override fun updateComment(): UpdateCommentUseCase = updateCommentUseCase

    override fun deleteComment(): DeleteCommentUseCase = deleteCommentUseCase
}
