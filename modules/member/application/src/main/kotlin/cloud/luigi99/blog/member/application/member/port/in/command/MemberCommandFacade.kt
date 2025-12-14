package cloud.luigi99.blog.member.application.member.port.`in`.command

interface MemberCommandFacade {
    fun updateProfile(): UpdateMemberProfileUseCase

    fun registerMember(): RegisterMemberUseCase

    fun deleteMember(): DeleteMemberUseCase
}
