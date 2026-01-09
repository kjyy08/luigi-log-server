package cloud.luigi99.blog.content.comment.application.port.`in`.query

/**
 * 댓글 목록 조회 유스케이스
 *
 * 게시글에 작성된 댓글 목록을 조회하는 기능을 정의합니다.
 */
interface GetCommentListUseCase {
    /**
     * 게시글의 댓글 목록을 조회합니다.
     */
    fun execute(query: Query): Response

    /**
     * 댓글 목록 조회 쿼리
     */
    data class Query(val postId: String)

    /**
     * 댓글 목록 조회 결과
     */
    data class Response(val comments: List<CommentInfo>)

    /**
     * 댓글 정보
     */
    data class CommentInfo(
        val commentId: String,
        val author: AuthorInfo,
        val content: String,
        val createdAt: String,
        val updatedAt: String,
    )

    /**
     * 작성자 정보
     */
    data class AuthorInfo(
        val memberId: String,
        val nickname: String,
        val profileImageUrl: String?,
        val username: String,
    )
}
