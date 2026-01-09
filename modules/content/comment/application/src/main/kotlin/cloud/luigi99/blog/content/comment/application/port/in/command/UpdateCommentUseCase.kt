package cloud.luigi99.blog.content.comment.application.port.`in`.command

/**
 * 댓글 수정 유스케이스
 *
 * 작성한 댓글의 내용을 수정하는 기능을 정의합니다.
 */
interface UpdateCommentUseCase {
    /**
     * 댓글을 수정합니다.
     */
    fun execute(command: Command): Response

    /**
     * 댓글 수정 명령
     */
    data class Command(val commentId: String, val authorId: String, val content: String)

    /**
     * 댓글 수정 결과
     */
    data class Response(
        val commentId: String,
        val author: AuthorInfo,
        val content: String,
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
