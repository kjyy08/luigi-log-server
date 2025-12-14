package cloud.luigi99.blog.auth.token.application.service.command

import cloud.luigi99.blog.auth.token.application.port.`in`.command.IssueTokenUseCase
import cloud.luigi99.blog.auth.token.application.port.out.AuthTokenRepository
import cloud.luigi99.blog.auth.token.application.port.out.TokenProvider
import cloud.luigi99.blog.auth.token.domain.model.AuthToken
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class IssueTokenService(
    private val tokenProvider: TokenProvider,
    private val authTokenRepository: AuthTokenRepository,
) : IssueTokenUseCase {
    @Transactional
    override fun execute(command: IssueTokenUseCase.Command): IssueTokenUseCase.Response {
        val memberId = MemberId.from(command.memberId)

        log.info { "Issuing tokens for member: $memberId" }

        authTokenRepository.deleteByMemberId(memberId)

        val accessToken = tokenProvider.generateAccessToken(memberId)
        val refreshToken = tokenProvider.generateRefreshToken(memberId)

        val accessTokenExpiration = tokenProvider.getExpiration(accessToken)
        val refreshTokenExpiration = tokenProvider.getExpiration(refreshToken)

        val authToken =
            AuthToken.issue(
                memberId = memberId,
                token = refreshToken,
                expirationMillis = refreshTokenExpiration * 1000,
            )
        authTokenRepository.save(authToken)

        log.info { "Successfully issued tokens for member: $memberId" }

        return IssueTokenUseCase.Response(
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpiration = accessTokenExpiration,
            refreshTokenExpiration = refreshTokenExpiration,
        )
    }
}
