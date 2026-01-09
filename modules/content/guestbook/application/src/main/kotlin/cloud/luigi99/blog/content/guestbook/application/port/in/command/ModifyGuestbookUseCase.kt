package cloud.luigi99.blog.content.guestbook.application.port.`in`.command

/**
 * 방명록 수정 유스케이스
 *
 * 작성자가 자신의 방명록을 수정합니다.
 */
interface ModifyGuestbookUseCase {
    /**
     * 방명록 수정을 실행합니다.
     */
    fun execute(command: Command): Response

    /**
     * 방명록 수정 명령
     */
    data class Command(val guestbookId: String, val requesterId: String, val content: String)

    /**
     * 방명록 수정 응답
     */
    data class Response(val guestbookId: String, val content: String)
}
