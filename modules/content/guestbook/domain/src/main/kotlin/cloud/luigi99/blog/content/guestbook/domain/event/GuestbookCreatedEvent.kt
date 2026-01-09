package cloud.luigi99.blog.content.guestbook.domain.event

import cloud.luigi99.blog.common.domain.event.DomainEvent
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookId
import cloud.luigi99.blog.member.domain.member.vo.MemberId

/**
 * 방명록 생성 이벤트
 *
 * 새로운 방명록이 작성되었을 때 발행되는 도메인 이벤트입니다.
 */
data class GuestbookCreatedEvent(val guestbookId: GuestbookId, val authorId: MemberId) : DomainEvent
