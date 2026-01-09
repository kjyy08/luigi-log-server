package cloud.luigi99.blog.content.comment.application.port.`in`.command

/**
 * 댓글 생성 유스케이스
 *
 * 게시글에 새로운 댓글을 작성하는 기능을 정의합니다.
 */
interface CreateCommentUseCase {
    /**
     * 댓글을 생성합니다.
     */
    fun execute(command: Command): Response

    /**
     * 댓글 생성 명령
     */
    data class Command(val postId: String, val authorId: String, val content: String)

    /**
     * 댓글 생성 결과
     */
    data class Response(
        val commentId: String,
        val postId: String,
        val author: AuthorInfo,
        val content: String,
        val createdAt: String,
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
