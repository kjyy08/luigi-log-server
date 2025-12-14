package cloud.luigi99.blog.auth.token.application.service.command

import cloud.luigi99.blog.auth.token.application.port.`in`.command.IssueTokenUseCase
import cloud.luigi99.blog.auth.token.application.port.`in`.command.ReissueTokenUseCase
import cloud.luigi99.blog.auth.token.application.port.`in`.command.RevokeTokenUseCase
import cloud.luigi99.blog.auth.token.application.port.`in`.command.TokenCommandFacade
import org.springframework.stereotype.Service

/**
 * 토큰 관리 Facade 구현체
 *
 * 토큰 발급, 재발급, 무효화, 삭제 유스케이스를 제공합니다.
 */
@Service
class TokenCommandService(
    private val issueTokenUseCase: IssueTokenUseCase,
    private val reissueTokenUseCase: ReissueTokenUseCase,
    private val revokeTokenUseCase: RevokeTokenUseCase,
) : TokenCommandFacade {
    override fun issue(): IssueTokenUseCase = issueTokenUseCase

    override fun reissue(): ReissueTokenUseCase = reissueTokenUseCase

    override fun revoke(): RevokeTokenUseCase = revokeTokenUseCase
}
