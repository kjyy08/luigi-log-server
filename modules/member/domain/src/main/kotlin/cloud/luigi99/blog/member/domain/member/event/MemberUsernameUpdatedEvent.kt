package cloud.luigi99.blog.member.domain.member.event

import cloud.luigi99.blog.common.domain.event.DomainEvent
import cloud.luigi99.blog.member.domain.member.vo.MemberId

/**
 * 회원 사용자 이름 변경 이벤트
 *
 * @property memberId 회원 ID
 * @property newUsername 새로운 사용자 이름
 */
data class MemberUsernameUpdatedEvent(val memberId: MemberId, val newUsername: String) : DomainEvent
