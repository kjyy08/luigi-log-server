package cloud.luigi99.blog.content.guestbook.application.service.query

import cloud.luigi99.blog.content.guestbook.application.port.`in`.query.GetGuestbookListUseCase
import cloud.luigi99.blog.content.guestbook.application.port.out.GuestbookRepository
import cloud.luigi99.blog.content.guestbook.application.port.out.MemberQueryPort
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 방명록 목록 조회 서비스
 */
@Service
class GetGuestbookListService(
    private val guestbookRepository: GuestbookRepository,
    private val memberQueryPort: MemberQueryPort,
) : GetGuestbookListUseCase {
    @Transactional(readOnly = true)
    override fun execute(): List<GetGuestbookListUseCase.Response> {
        log.info { "Getting all guestbooks" }

        val guestbooks = guestbookRepository.findAllOrderByCreatedAtDesc()

        if (guestbooks.isEmpty()) return emptyList()

        val authorIds =
            guestbooks
                .map {
                    it.authorId.value
                        .toString()
                }.distinct()
        val authors = memberQueryPort.getAuthors(authorIds)

        return guestbooks.map { guestbook ->
            val authorId =
                guestbook.authorId.value
                    .toString()
            val author =
                authors[authorId] ?: MemberQueryPort.Author(
                    memberId = authorId,
                    nickname = "Unknown",
                    profileImageUrl = null,
                    username = "unknown",
                )

            GetGuestbookListUseCase.Response(
                guestbookId =
                    guestbook.entityId.value
                        .toString(),
                author =
                    GetGuestbookListUseCase.AuthorInfo(
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
}
