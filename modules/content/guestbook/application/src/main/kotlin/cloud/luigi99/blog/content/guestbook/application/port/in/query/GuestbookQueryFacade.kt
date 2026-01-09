package cloud.luigi99.blog.content.guestbook.application.port.`in`.query

/**
 * 방명록 조회 파사드
 *
 * 방명록 관련 Query UseCase들을 그룹화합니다.
 */
interface GuestbookQueryFacade {
    /**
     * 단일 방명록 조회 유스케이스를 반환합니다.
     */
    fun getGuestbook(): GetGuestbookUseCase

    /**
     * 방명록 목록 조회 유스케이스를 반환합니다.
     */
    fun getGuestbookList(): GetGuestbookListUseCase
}
