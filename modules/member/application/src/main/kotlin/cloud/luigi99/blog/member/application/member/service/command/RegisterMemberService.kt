package cloud.luigi99.blog.member.application.member.service.command

import cloud.luigi99.blog.member.application.member.port.`in`.command.RegisterMemberUseCase
import cloud.luigi99.blog.member.application.member.port.out.MemberRepository
import cloud.luigi99.blog.member.domain.member.model.Member
import cloud.luigi99.blog.member.domain.member.vo.Email
import cloud.luigi99.blog.member.domain.member.vo.Username
import cloud.luigi99.blog.member.domain.profile.model.Profile
import cloud.luigi99.blog.member.domain.profile.vo.Nickname
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 회원 등록 유스케이스 구현체
 *
 * OAuth2 인증을 통한 신규 회원 등록을 처리합니다.
 * 회원 생성과 기본 프로필 생성을 수행합니다.
 */
@Service
class RegisterMemberService(private val memberRepository: MemberRepository) : RegisterMemberUseCase {
    @Transactional
    override fun execute(command: RegisterMemberUseCase.Command): RegisterMemberUseCase.Response {
        log.info { "Registering new member with email: ${command.email}" }

        val email = Email(command.email)
        val username = Username(command.username)

        // 회원 생성
        val newMember =
            Member.register(
                email = email,
                username = username,
                profile = null,
            )

        // 프로필 생성
        val profile =
            Profile.create(
                nickname = Nickname(command.username),
            )

        // 프로필 연결 및 저장
        val updatedMember = newMember.updateProfile(profile)
        val savedMember = memberRepository.save(updatedMember)

        log.info { "Successfully registered member: ${savedMember.entityId}" }

        return RegisterMemberUseCase.Response(
            memberId =
                savedMember.entityId.value
                    .toString(),
            email = savedMember.email.value,
            username = savedMember.username.value,
        )
    }
}
