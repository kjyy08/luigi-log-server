package cloud.luigi99.blog.member.application.member.service.query

import cloud.luigi99.blog.member.application.member.port.`in`.query.GetCurrentMemberUseCase
import cloud.luigi99.blog.member.application.member.port.`in`.query.GetMemberProfileUseCase
import cloud.luigi99.blog.member.application.member.port.`in`.query.GetMembersProfileUseCase
import cloud.luigi99.blog.member.application.member.port.`in`.query.MemberQueryFacade
import org.springframework.stereotype.Service

@Service
class MemberQueryService(
    private val getCurrentMemberUseCase: GetCurrentMemberUseCase,
    private val getMemberProfileUseCase: GetMemberProfileUseCase,
    private val getMembersProfileUseCase: GetMembersProfileUseCase,
) : MemberQueryFacade {
    override fun getCurrentMember(): GetCurrentMemberUseCase = getCurrentMemberUseCase

    override fun getMemberProfile(): GetMemberProfileUseCase = getMemberProfileUseCase

    override fun getMembersProfile(): GetMembersProfileUseCase = getMembersProfileUseCase
}
