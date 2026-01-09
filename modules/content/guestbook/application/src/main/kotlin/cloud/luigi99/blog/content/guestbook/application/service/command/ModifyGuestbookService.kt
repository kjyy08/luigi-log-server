package cloud.luigi99.blog.content.guestbook.application.service.command

import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.ModifyGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.out.GuestbookRepository
import cloud.luigi99.blog.content.guestbook.domain.exception.GuestbookNotFoundException
import cloud.luigi99.blog.content.guestbook.domain.exception.UnauthorizedGuestbookAccessException
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookContent
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 방명록 수정 서비스
 *
 * 방명록 내용 수정을 처리합니다.
 */
@Service
class ModifyGuestbookService(private val guestbookRepository: GuestbookRepository) : ModifyGuestbookUseCase {
    @Transactional
    override fun execute(command: ModifyGuestbookUseCase.Command): ModifyGuestbookUseCase.Response {
        log.info { "Modifying guestbook: ${command.guestbookId}" }

        val guestbookId = GuestbookId.from(command.guestbookId)
        val requesterId = MemberId.from(command.requesterId)

        val guestbook =
            guestbookRepository.findById(guestbookId)
                ?: throw GuestbookNotFoundException()

        if (!guestbook.isOwnedBy(requesterId)) {
            throw UnauthorizedGuestbookAccessException()
        }

        val newContent = GuestbookContent(command.content)
        val updatedGuestbook = guestbook.updateContent(newContent)
        val savedGuestbook = guestbookRepository.save(updatedGuestbook)

        log.info { "Successfully modified guestbook: ${savedGuestbook.entityId}" }

        return ModifyGuestbookUseCase.Response(
            guestbookId =
                savedGuestbook.entityId.value
                    .toString(),
            content = savedGuestbook.content.value,
        )
    }
}
