package cloud.luigi99.blog.member.application.member.port.`in`.query

interface MemberQueryFacade {
    fun getCurrentMember(): GetCurrentMemberUseCase

    fun getMemberProfile(): GetMemberProfileUseCase
}
