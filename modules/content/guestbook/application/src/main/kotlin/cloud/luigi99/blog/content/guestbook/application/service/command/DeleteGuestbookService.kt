package cloud.luigi99.blog.content.guestbook.application.service.command

import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.DeleteGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.out.GuestbookRepository
import cloud.luigi99.blog.content.guestbook.domain.exception.GuestbookNotFoundException
import cloud.luigi99.blog.content.guestbook.domain.exception.UnauthorizedGuestbookAccessException
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 방명록 삭제 서비스
 *
 * 방명록 삭제를 처리합니다.
 */
@Service
class DeleteGuestbookService(private val guestbookRepository: GuestbookRepository) : DeleteGuestbookUseCase {
    @Transactional
    override fun execute(command: DeleteGuestbookUseCase.Command) {
        log.info { "Deleting guestbook: ${command.guestbookId}" }

        val guestbookId = GuestbookId.from(command.guestbookId)
        val requesterId = MemberId.from(command.requesterId)

        val guestbook =
            guestbookRepository.findById(guestbookId)
                ?: throw GuestbookNotFoundException()

        if (!guestbook.isOwnedBy(requesterId)) {
            throw UnauthorizedGuestbookAccessException()
        }

        guestbookRepository.deleteById(guestbookId)

        log.info { "Successfully deleted guestbook: $guestbookId" }
    }
}
