package cloud.luigi99.blog.content.guestbook.domain.model

import cloud.luigi99.blog.common.domain.AggregateRoot
import cloud.luigi99.blog.content.guestbook.domain.event.GuestbookCreatedEvent
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookContent
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import java.time.LocalDateTime

/**
 * 방명록 애그리거트 루트
 *
 * 블로그 방명록의 핵심 도메인 엔티티로, 방명록의 생성, 수정 등 비즈니스 로직을 담당합니다.
 */
class Guestbook private constructor(
    override val entityId: GuestbookId,
    val authorId: MemberId,
    content: GuestbookContent,
) : AggregateRoot<GuestbookId>() {
    var content: GuestbookContent = content
        private set

    companion object {
        /**
         * 새 방명록을 생성합니다.
         */
        fun create(authorId: MemberId, content: GuestbookContent): Guestbook {
            val guestbook =
                Guestbook(
                    entityId = GuestbookId.generate(),
                    authorId = authorId,
                    content = content,
                )
            guestbook.registerEvent(GuestbookCreatedEvent(guestbook.entityId, authorId))
            return guestbook
        }

        /**
         * 영속성 계층에서 데이터를 로드하여 도메인 엔티티를 재구성합니다.
         */
        fun from(
            entityId: GuestbookId,
            authorId: MemberId,
            content: GuestbookContent,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Guestbook {
            val guestbook = Guestbook(entityId, authorId, content)
            guestbook.createdAt = createdAt
            guestbook.updatedAt = updatedAt
            return guestbook
        }
    }

    /**
     * 방명록 내용을 수정합니다.
     */
    fun updateContent(newContent: GuestbookContent): Guestbook {
        this.content = newContent
        return this
    }

    /**
     * 해당 회원이 이 방명록의 작성자인지 확인합니다.
     */
    fun isOwnedBy(memberId: MemberId): Boolean = authorId == memberId
}
