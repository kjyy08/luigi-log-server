package cloud.luigi99.blog.content.guestbook.application.service.command

import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.CreateGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.out.GuestbookRepository
import cloud.luigi99.blog.content.guestbook.application.port.out.MemberQueryPort
import cloud.luigi99.blog.content.guestbook.domain.model.Guestbook
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookContent
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 방명록 작성 서비스
 *
 * 새 방명록 글 작성을 처리합니다.
 */
@Service
class CreateGuestbookService(
    private val guestbookRepository: GuestbookRepository,
    private val memberQueryPort: MemberQueryPort,
) : CreateGuestbookUseCase {
    @Transactional
    override fun execute(command: CreateGuestbookUseCase.Command): CreateGuestbookUseCase.Response {
        log.info { "Creating new guestbook entry by author: ${command.authorId}" }

        val authorId = MemberId.from(command.authorId)
        val content = GuestbookContent(command.content)

        val guestbook = Guestbook.create(authorId, content)
        val savedGuestbook = guestbookRepository.save(guestbook)

        val author = memberQueryPort.getAuthor(command.authorId)

        log.info { "Successfully created guestbook: ${savedGuestbook.entityId}" }

        return CreateGuestbookUseCase.Response(
            guestbookId =
                savedGuestbook.entityId.value
                    .toString(),
            author =
                CreateGuestbookUseCase.AuthorInfo(
                    memberId = author.memberId,
                    nickname = author.nickname,
                    profileImageUrl = author.profileImageUrl,
                    username = author.username,
                ),
            content = savedGuestbook.content.value,
        )
    }
}
