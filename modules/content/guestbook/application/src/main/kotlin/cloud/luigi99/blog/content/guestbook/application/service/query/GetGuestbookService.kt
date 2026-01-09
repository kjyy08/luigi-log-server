package cloud.luigi99.blog.content.guestbook.application.service.query

import cloud.luigi99.blog.content.guestbook.application.port.`in`.query.GetGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.out.GuestbookRepository
import cloud.luigi99.blog.content.guestbook.application.port.out.MemberQueryPort
import cloud.luigi99.blog.content.guestbook.domain.exception.GuestbookNotFoundException
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 단일 방명록 조회 서비스
 */
@Service
class GetGuestbookService(
    private val guestbookRepository: GuestbookRepository,
    private val memberQueryPort: MemberQueryPort,
) : GetGuestbookUseCase {
    @Transactional(readOnly = true)
    override fun execute(query: GetGuestbookUseCase.Query): GetGuestbookUseCase.Response {
        log.info { "Getting guestbook: ${query.guestbookId}" }

        val guestbookId = GuestbookId.from(query.guestbookId)
        val guestbook =
            guestbookRepository.findById(guestbookId)
                ?: throw GuestbookNotFoundException()

        val author =
            memberQueryPort.getAuthor(
                guestbook.authorId.value
                    .toString(),
            )

        return GetGuestbookUseCase.Response(
            guestbookId =
                guestbook.entityId.value
                    .toString(),
            author =
                GetGuestbookUseCase.AuthorInfo(
                    memberId = author.memberId,
                    nickname = author.nickname,
                    profileImageUrl = author.profileImageUrl,
                    username = author.username,
                ),
            content = guestbook.content.value,
            createdAt = guestbook.createdAt?.toString() ?: "",
            updatedAt = guestbook.updatedAt?.toString() ?: "",
        )
    }
}
