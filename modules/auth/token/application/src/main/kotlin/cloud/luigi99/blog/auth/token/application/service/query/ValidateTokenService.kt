package cloud.luigi99.blog.auth.token.application.service.query

import cloud.luigi99.blog.auth.token.application.port.`in`.query.ValidateTokenUseCase
import cloud.luigi99.blog.auth.token.application.port.out.TokenProvider
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

/**
 * 토큰 검증 유스케이스 구현체
 *
 * JWT 토큰의 유효성을 검증하고 토큰에 포함된 정보를 추출합니다.
 */
@Service
class ValidateTokenService(private val tokenProvider: TokenProvider) : ValidateTokenUseCase {
    override fun execute(query: ValidateTokenUseCase.Query): ValidateTokenUseCase.Response {
        log.debug { "Validating token" }

        if (!tokenProvider.validateToken(query.token)) {
            log.debug { "Token validation failed" }
            return ValidateTokenUseCase.Response(
                isValid = false,
                memberId = null,
                expiration = null,
            )
        }

        val memberId = tokenProvider.getSubject(query.token)
        val expiration = tokenProvider.getExpiration(query.token)

        log.debug { "Token validated successfully for member: $memberId" }

        return ValidateTokenUseCase.Response(
            isValid = true,
            memberId = memberId,
            expiration = expiration,
        )
    }
}
