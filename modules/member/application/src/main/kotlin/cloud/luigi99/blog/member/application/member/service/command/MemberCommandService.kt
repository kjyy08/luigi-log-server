package cloud.luigi99.blog.member.application.member.service.command

import cloud.luigi99.blog.member.application.member.port.`in`.command.DeleteMemberUseCase
import cloud.luigi99.blog.member.application.member.port.`in`.command.MemberCommandFacade
import cloud.luigi99.blog.member.application.member.port.`in`.command.RegisterMemberUseCase
import cloud.luigi99.blog.member.application.member.port.`in`.command.UpdateMemberProfileUseCase
import org.springframework.stereotype.Service

/**
 * 회원 관리 Facade 구현체
 *
 * 회원 등록, 프로필 업데이트, 회원 탈퇴 유스케이스를 제공합니다.
 */
@Service
class MemberCommandService(
    private val updateMemberProfileUseCase: UpdateMemberProfileUseCase,
    private val registerMemberUseCase: RegisterMemberUseCase,
    private val deleteMemberUseCase: DeleteMemberUseCase,
) : MemberCommandFacade {
    override fun updateProfile(): UpdateMemberProfileUseCase = updateMemberProfileUseCase

    override fun registerMember(): RegisterMemberUseCase = registerMemberUseCase

    override fun deleteMember(): DeleteMemberUseCase = deleteMemberUseCase
}
