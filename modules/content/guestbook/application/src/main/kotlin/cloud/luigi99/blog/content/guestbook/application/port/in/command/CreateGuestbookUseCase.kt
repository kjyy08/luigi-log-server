package cloud.luigi99.blog.content.guestbook.application.port.`in`.command

/**
 * 방명록 작성 유스케이스
 *
 * 로그인한 회원이 새 방명록을 작성합니다.
 */
interface CreateGuestbookUseCase {
    /**
     * 방명록 작성을 실행합니다.
     */
    fun execute(command: Command): Response

    /**
     * 방명록 작성 명령
     */
    data class Command(val authorId: String, val content: String)

    /**
     * 방명록 작성 응답
     */
    data class Response(val guestbookId: String, val author: AuthorInfo, val content: String)

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
