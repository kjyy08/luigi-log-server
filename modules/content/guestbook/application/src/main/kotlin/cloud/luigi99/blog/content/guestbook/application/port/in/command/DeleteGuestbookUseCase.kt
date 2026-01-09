package cloud.luigi99.blog.content.guestbook.application.port.`in`.command

/**
 * 방명록 삭제 유스케이스
 *
 * 작성자가 자신의 방명록을 삭제합니다.
 */
interface DeleteGuestbookUseCase {
    /**
     * 방명록 삭제를 실행합니다.
     */
    fun execute(command: Command)

    /**
     * 방명록 삭제 명령
     */
    data class Command(val guestbookId: String, val requesterId: String)
}
