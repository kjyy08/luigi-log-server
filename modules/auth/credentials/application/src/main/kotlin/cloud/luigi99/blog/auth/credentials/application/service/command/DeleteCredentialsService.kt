package cloud.luigi99.blog.auth.credentials.application.service.command

import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.DeleteCredentialsUseCase
import cloud.luigi99.blog.auth.credentials.application.port.out.MemberCredentialsRepository
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 회원 인증 정보 삭제 서비스
 *
 * 회원 탈퇴 시 인증 정보를 삭제합니다.
 */
@Service
class DeleteCredentialsService(private val memberCredentialsRepository: MemberCredentialsRepository) :
    DeleteCredentialsUseCase {
    @Transactional
    override fun execute(command: DeleteCredentialsUseCase.Command) {
        val memberId = MemberId.from(command.memberId)

        log.info { "Deleting credentials for member: $memberId" }

        memberCredentialsRepository.deleteByMemberId(memberId)

        log.info { "Successfully deleted credentials for member: $memberId" }
    }
}
