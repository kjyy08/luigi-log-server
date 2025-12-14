package cloud.luigi99.blog.member.domain.member.event

import cloud.luigi99.blog.common.domain.event.DomainEvent
import cloud.luigi99.blog.member.domain.member.vo.MemberId

/**
 * 회원 탈퇴 이벤트
 *
 * 회원이 탈퇴했을 때 발생하는 도메인 이벤트입니다.
 */
data class MemberDeletedEvent(val memberId: MemberId) : DomainEvent
