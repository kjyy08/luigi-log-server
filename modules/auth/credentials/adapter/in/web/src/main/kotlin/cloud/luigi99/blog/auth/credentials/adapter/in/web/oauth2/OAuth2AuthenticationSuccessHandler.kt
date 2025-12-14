package cloud.luigi99.blog.auth.credentials.adapter.`in`.web.oauth2

import cloud.luigi99.blog.adapter.web.util.CookieUtils
import cloud.luigi99.blog.auth.credentials.adapter.`in`.web.config.OAuth2Properties
import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.CredentialsCommandFacade
import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.LoginUseCase
import cloud.luigi99.blog.auth.token.application.port.`in`.command.IssueTokenUseCase
import cloud.luigi99.blog.auth.token.application.port.`in`.command.TokenCommandFacade
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

private val log = KotlinLogging.logger {}

@Component
class OAuth2AuthenticationSuccessHandler(
    private val credentialsCommandFacade: CredentialsCommandFacade,
    private val tokenCommandFacade: TokenCommandFacade,
    private val cookieUtils: CookieUtils,
    private val oAuth2Properties: OAuth2Properties,
) : SimpleUrlAuthenticationSuccessHandler() {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        log.info { "OAuth2 authentication successful" }

        val oauth2Token = authentication as OAuth2AuthenticationToken
        val oauth2User = oauth2Token.principal as OAuth2User
        val provider = oauth2Token.authorizedClientRegistrationId

        log.debug { "OAuth2 provider: $provider" }

        try {
            val userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oauth2User)

            log.debug {
                "Extracted user info - email: ${userInfo.getEmail()}, " +
                    "username: ${userInfo.getUsername()}, providerId: ${userInfo.getProviderId()}"
            }

            val loginResponse =
                credentialsCommandFacade.login().execute(
                    LoginUseCase.Command(
                        email = userInfo.getEmail(),
                        username = userInfo.getUsername(),
                        provider = provider,
                        providerId = userInfo.getProviderId(),
                    ),
                )

            log.info { "Member authenticated successfully: ${loginResponse.memberId}" }

            val tokenResponse =
                tokenCommandFacade.issue().execute(
                    IssueTokenUseCase.Command(
                        memberId = loginResponse.memberId,
                    ),
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

            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())

            val successUrl = oAuth2Properties.baseUrl + oAuth2Properties.endPoint.success
            log.debug { "Redirecting to: $successUrl" }
            redirectStrategy.sendRedirect(request, response, successUrl)
        } catch (e: Exception) {
            log.error(e) { "Error processing OAuth2 authentication" }

            val errorUrl =
                UriComponentsBuilder
                    .fromUriString(oAuth2Properties.baseUrl + oAuth2Properties.endPoint.error)
                    .queryParam("error", "Authentication failed")
                    .build()
                    .toUriString()

            redirectStrategy.sendRedirect(request, response, errorUrl)
        }
    }
}
