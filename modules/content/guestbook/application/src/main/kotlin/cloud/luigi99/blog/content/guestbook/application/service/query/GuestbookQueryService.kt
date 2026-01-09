package cloud.luigi99.blog.content.guestbook.application.service.query

import cloud.luigi99.blog.content.guestbook.application.port.`in`.query.GetGuestbookListUseCase
import cloud.luigi99.blog.content.guestbook.application.port.`in`.query.GetGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.`in`.query.GuestbookQueryFacade
import org.springframework.stereotype.Service

/**
 * 방명록 조회 파사드 구현체
 *
 * Query UseCase들을 그룹화하여 제공합니다.
 */
@Service
class GuestbookQueryService(
    private val getGuestbookUseCase: GetGuestbookUseCase,
    private val getGuestbookListUseCase: GetGuestbookListUseCase,
) : GuestbookQueryFacade {
    override fun getGuestbook(): GetGuestbookUseCase = getGuestbookUseCase

    override fun getGuestbookList(): GetGuestbookListUseCase = getGuestbookListUseCase
}
