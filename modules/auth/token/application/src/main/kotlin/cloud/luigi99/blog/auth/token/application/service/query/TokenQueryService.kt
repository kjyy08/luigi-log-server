package cloud.luigi99.blog.auth.token.application.service.query

import cloud.luigi99.blog.auth.token.application.port.`in`.query.TokenQueryFacade
import cloud.luigi99.blog.auth.token.application.port.`in`.query.ValidateTokenUseCase
import org.springframework.stereotype.Service

/**
 * 토큰 조회 Facade 구현체
 *
 * 토큰 검증 유스케이스를 제공합니다.
 */
@Service
class TokenQueryService(private val validateTokenUseCase: ValidateTokenUseCase) : TokenQueryFacade {
    override fun validate(): ValidateTokenUseCase = validateTokenUseCase
}
