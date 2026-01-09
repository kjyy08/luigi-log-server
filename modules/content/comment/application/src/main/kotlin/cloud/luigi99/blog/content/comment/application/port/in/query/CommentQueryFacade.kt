package cloud.luigi99.blog.content.comment.application.port.`in`.query

/**
 * 댓글 조회 파사드
 *
 * 댓글 조회와 관련된 유스케이스들을 제공합니다.
 */
interface CommentQueryFacade {
    /**
     * 댓글 목록 조회 유스케이스를 반환합니다.
     */
    fun getCommentList(): GetCommentListUseCase
}
