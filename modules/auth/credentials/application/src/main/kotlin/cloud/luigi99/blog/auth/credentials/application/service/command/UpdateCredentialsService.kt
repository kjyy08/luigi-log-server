package cloud.luigi99.blog.auth.credentials.application.service.command

import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.UpdateCredentialsUseCase
import cloud.luigi99.blog.auth.credentials.application.port.out.MemberCredentialsRepository
import cloud.luigi99.blog.auth.credentials.domain.exception.MemberCredentialsException
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 인증 정보 업데이트 유스케이스 구현체
 *
 * 회원의 권한을 변경합니다.
 */
@Service
class UpdateCredentialsService(private val memberCredentialsRepository: MemberCredentialsRepository) :
    UpdateCredentialsUseCase {
    /**
     * 인증 정보 업데이트 명령을 실행합니다.
     *
     * @param command 업데이트 요청 명령 객체
     * @throws MemberCredentialsException 인증 정보를 찾을 수 없는 경우
     */
    @Transactional
    override fun execute(command: UpdateCredentialsUseCase.Command) {
        log.info { "Updating credentials for member: ${command.memberId}" }

        val memberId = MemberId.from(command.memberId)
        val credentials =
            memberCredentialsRepository.findByMemberId(memberId)
                ?: throw MemberCredentialsException()

        credentials.updateRole(command.role)
        memberCredentialsRepository.save(credentials)

        log.info { "Successfully updated credentials for member ${command.memberId} to role ${command.role}" }
    }
}
