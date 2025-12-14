package cloud.luigi99.blog.auth.credentials.domain.event

import cloud.luigi99.blog.common.domain.event.DomainEvent
import cloud.luigi99.blog.member.domain.member.vo.MemberId

data class MemberLoggedInEvent(val memberId: MemberId) : DomainEvent
