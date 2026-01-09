package cloud.luigi99.blog.content.comment.application.port.`in`.command

/**
 * 댓글 명령 파사드
 *
 * 댓글 생성, 수정, 삭제와 관련된 유스케이스들을 제공합니다.
 */
interface CommentCommandFacade {
    /**
     * 댓글 생성 유스케이스를 반환합니다.
     */
    fun createComment(): CreateCommentUseCase

    /**
     * 댓글 수정 유스케이스를 반환합니다.
     */
    fun updateComment(): UpdateCommentUseCase

    /**
     * 댓글 삭제 유스케이스를 반환합니다.
     */
    fun deleteComment(): DeleteCommentUseCase
}
