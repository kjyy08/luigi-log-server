package cloud.luigi99.blog.auth.token.adapter.`in`.web

import cloud.luigi99.blog.adapter.web.util.CookieUtils
import cloud.luigi99.blog.auth.token.application.port.`in`.command.ReissueTokenUseCase
import cloud.luigi99.blog.auth.token.application.port.`in`.command.RevokeTokenUseCase
import cloud.luigi99.blog.auth.token.application.port.`in`.command.TokenCommandFacade
import cloud.luigi99.blog.auth.token.domain.exception.InvalidTokenException
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

/**
 * 인증 관리 컨트롤러
 *
 * 토큰 갱신 및 로그아웃 등 인증 관련 요청을 처리합니다.
 */
@RestController
@RequestMapping("/api/v1/auth/tokens")
class AuthTokenController(private val tokenCommandFacade: TokenCommandFacade, private val cookieUtils: CookieUtils) :
    AuthTokenApi {
    @PostMapping("/reissue")
    override fun reissue(
        @RequestHeader("Authorization") refreshToken: String,
    ): ResponseEntity<Unit> {
        log.info("Reissuing tokens for refresh token: $refreshToken")

        val extractedToken = extractRefreshToken(refreshToken)

        val tokenResponse =
            tokenCommandFacade.reissue().execute(
                ReissueTokenUseCase.Command(extractedToken),
            )

        val accessTokenCookie =
            cookieUtils.createCookie(
                name = "accessToken",
                value = tokenResponse.accessToken,
                maxAge = tokenResponse.accessTokenExpiration,
            )

        val refreshTokenCookie =
            cookieUtils.createCookie(
                name = "refreshToken",
                value = tokenResponse.refreshToken,
                maxAge = tokenResponse.refreshTokenExpiration,
            )

        val headers =
            HttpHeaders().apply {
                add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            }

        return ResponseEntity
            .noContent()
            .headers(headers)
            .build()
    }

    @PostMapping("/logout")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    override fun logout(
        @AuthenticationPrincipal memberId: String,
    ): ResponseEntity<Unit> {
        log.info("Logging out for member: $memberId")

        tokenCommandFacade.revoke().execute(
            RevokeTokenUseCase.Command(memberId),
        )

        val accessTokenCookie = cookieUtils.deleteCookie("accessToken")
        val refreshTokenCookie = cookieUtils.deleteCookie("refreshToken")

        val headers =
            HttpHeaders().apply {
                add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            }

        return ResponseEntity
            .noContent()
            .headers(headers)
            .build()
    }

    private fun extractRefreshToken(token: String): String {
        if (!token.startsWith("Bearer ")) {
            log.warn { "Authorization header does not start with Bearer: $token" }
            throw InvalidTokenException("Authorization header must start with Bearer")
        }
        return token.replace("Bearer ", "")
    }
}
