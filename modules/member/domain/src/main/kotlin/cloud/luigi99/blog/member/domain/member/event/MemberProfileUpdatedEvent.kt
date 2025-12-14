package cloud.luigi99.blog.member.domain.member.event

import cloud.luigi99.blog.common.domain.event.DomainEvent
import cloud.luigi99.blog.member.domain.member.vo.MemberId

/**
 * 회원 프로필 수정 이벤트
 *
 * @property memberId 회원 ID
 */
data class MemberProfileUpdatedEvent(val memberId: MemberId) : DomainEvent
