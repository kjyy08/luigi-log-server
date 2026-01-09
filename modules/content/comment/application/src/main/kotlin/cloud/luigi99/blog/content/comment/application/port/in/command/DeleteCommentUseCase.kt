package cloud.luigi99.blog.content.comment.application.port.`in`.command

/**
 * 댓글 삭제 유스케이스
 *
 * 작성한 댓글을 삭제하는 기능을 정의합니다.
 */
interface DeleteCommentUseCase {
    /**
     * 댓글을 삭제합니다.
     */
    fun execute(command: Command)

    /**
     * 댓글 삭제 명령
     */
    data class Command(val commentId: String, val authorId: String)
}
