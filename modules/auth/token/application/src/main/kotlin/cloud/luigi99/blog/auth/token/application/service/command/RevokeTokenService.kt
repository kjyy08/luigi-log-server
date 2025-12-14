package cloud.luigi99.blog.auth.token.application.service.command

import cloud.luigi99.blog.auth.token.application.port.`in`.command.RevokeTokenUseCase
import cloud.luigi99.blog.auth.token.application.port.out.AuthTokenRepository
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 토큰 무효화 서비스
 *
 * 리프레시 토큰을 저장소에서 삭제하여 로그아웃을 처리합니다.
 */
@Service
class RevokeTokenService(private val authTokenRepository: AuthTokenRepository) : RevokeTokenUseCase {
    @Transactional
    override fun execute(command: RevokeTokenUseCase.Command) {
        val memberId = MemberId.from(command.memberId)
        log.info { "Revoking refresh token for member: $memberId" }

        authTokenRepository.deleteByMemberId(memberId)

        log.info { "Successfully revoked refresh token for member: $memberId" }
    }
}
