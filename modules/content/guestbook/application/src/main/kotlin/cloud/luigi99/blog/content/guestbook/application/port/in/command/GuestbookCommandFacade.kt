package cloud.luigi99.blog.content.guestbook.application.port.`in`.command

/**
 * 방명록 명령 파사드
 *
 * 방명록 관련 Command UseCase들을 그룹화합니다.
 */
interface GuestbookCommandFacade {
    /**
     * 방명록 작성 유스케이스를 반환합니다.
     */
    fun createGuestbook(): CreateGuestbookUseCase

    /**
     * 방명록 수정 유스케이스를 반환합니다.
     */
    fun modifyGuestbook(): ModifyGuestbookUseCase

    /**
     * 방명록 삭제 유스케이스를 반환합니다.
     */
    fun deleteGuestbook(): DeleteGuestbookUseCase
}
