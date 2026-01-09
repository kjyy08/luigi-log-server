package cloud.luigi99.blog.content.guestbook.application.service.command

import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.CreateGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.DeleteGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.GuestbookCommandFacade
import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.ModifyGuestbookUseCase
import org.springframework.stereotype.Service

/**
 * 방명록 명령 파사드 구현체
 *
 * Command UseCase들을 그룹화하여 제공합니다.
 */
@Service
class GuestbookCommandService(
    private val createGuestbookUseCase: CreateGuestbookUseCase,
    private val modifyGuestbookUseCase: ModifyGuestbookUseCase,
    private val deleteGuestbookUseCase: DeleteGuestbookUseCase,
) : GuestbookCommandFacade {
    override fun createGuestbook(): CreateGuestbookUseCase = createGuestbookUseCase

    override fun modifyGuestbook(): ModifyGuestbookUseCase = modifyGuestbookUseCase

    override fun deleteGuestbook(): DeleteGuestbookUseCase = deleteGuestbookUseCase
}
