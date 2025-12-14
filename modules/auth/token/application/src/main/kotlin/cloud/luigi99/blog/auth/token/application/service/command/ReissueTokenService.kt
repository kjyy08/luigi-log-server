package cloud.luigi99.blog.auth.token.application.service.command

import cloud.luigi99.blog.auth.token.application.port.`in`.command.ReissueTokenUseCase
import cloud.luigi99.blog.auth.token.application.port.out.AuthTokenRepository
import cloud.luigi99.blog.auth.token.application.port.out.TokenProvider
import cloud.luigi99.blog.auth.token.domain.exception.InvalidTokenException
import cloud.luigi99.blog.auth.token.domain.model.AuthToken
import cloud.luigi99.blog.auth.token.domain.model.AuthToken.TokenStatus.*
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

private val log = KotlinLogging.logger {}

@Service
class ReissueTokenService(
    private val authTokenRepository: AuthTokenRepository,
    private val tokenProvider: TokenProvider,
) : ReissueTokenUseCase {
    @Transactional
    override fun execute(command: ReissueTokenUseCase.Command): ReissueTokenUseCase.Response {
        val refreshToken = command.refreshToken

        if (!tokenProvider.validateToken(refreshToken)) {
            throw InvalidTokenException("Invalid refresh token signature/format")
        }

        val memberId = MemberId(UUID.fromString(tokenProvider.getSubject(refreshToken)))

        val storedAuthToken =
            authTokenRepository.findByMemberId(memberId)
                ?: run {
                    log.warn { "No stored refresh token found for member: $memberId" }
                    throw InvalidTokenException()
                }

        validateStoredToken(storedAuthToken, refreshToken)

        val newAccessToken = tokenProvider.generateAccessToken(memberId)
        val newRefreshToken = tokenProvider.generateRefreshToken(memberId)
        val refreshTokenExpiration = tokenProvider.getExpiration(newRefreshToken)

        AuthToken
            .issue(
                memberId = memberId,
                token = newRefreshToken,
                expirationMillis = refreshTokenExpiration * 1000,
            ).also { authTokenRepository.save(it) }

        log.info { "Successfully refreshed tokens for member: $memberId" }

        return ReissueTokenUseCase.Response(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
            accessTokenExpiration = tokenProvider.getExpiration(newAccessToken),
            refreshTokenExpiration = refreshTokenExpiration,
        )
    }

    private fun validateStoredToken(storedToken: AuthToken, incomingToken: String) {
        when (storedToken.validateRotation(incomingToken)) {
            THEFT_DETECTED -> {
                log.error { "Token theft detected for member: ${storedToken.memberId}" }
                authTokenRepository.deleteByMemberId(storedToken.memberId)
                throw InvalidTokenException("Invalid refresh token (Reuse detected)")
            }

            EXPIRED -> {
                log.warn { "Refresh token expired for member: ${storedToken.memberId}" }
                authTokenRepository.deleteByMemberId(storedToken.memberId)
                throw InvalidTokenException("Refresh token expired")
            }

            VALID -> {
                log.debug { "Valid refresh token found for member: ${storedToken.memberId}" }
            }
        }
    }
}
