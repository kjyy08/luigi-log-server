package cloud.luigi99.blog.member.application.member.port.out

import cloud.luigi99.blog.common.application.port.out.Repository
import cloud.luigi99.blog.member.domain.member.model.Member
import cloud.luigi99.blog.member.domain.member.vo.Email
import cloud.luigi99.blog.member.domain.member.vo.MemberId

interface MemberRepository : Repository<Member, MemberId> {
    fun findByEmail(email: Email): Member?

    fun existsByEmail(email: Email): Boolean
}
