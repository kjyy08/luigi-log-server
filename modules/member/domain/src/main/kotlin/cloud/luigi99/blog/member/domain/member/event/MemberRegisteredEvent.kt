package cloud.luigi99.blog.member.domain.member.event

import cloud.luigi99.blog.common.domain.event.DomainEvent
import cloud.luigi99.blog.member.domain.member.vo.Email
import cloud.luigi99.blog.member.domain.member.vo.MemberId

data class MemberRegisteredEvent(val memberId: MemberId, val email: Email) : DomainEvent
