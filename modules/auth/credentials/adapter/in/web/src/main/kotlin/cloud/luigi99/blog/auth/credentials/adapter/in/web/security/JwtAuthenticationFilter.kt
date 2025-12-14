package cloud.luigi99.blog.auth.credentials.adapter.`in`.web.security

import cloud.luigi99.blog.auth.credentials.application.port.`in`.query.CredentialsQueryFacade
import cloud.luigi99.blog.auth.credentials.application.port.`in`.query.GetMemberCredentialsUseCase
import cloud.luigi99.blog.auth.token.application.port.`in`.query.TokenQueryFacade
import cloud.luigi99.blog.auth.token.application.port.`in`.query.ValidateTokenUseCase
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

private val log = KotlinLogging.logger {}

@Component
class JwtAuthenticationFilter(
    private val credentialsQueryFacade: CredentialsQueryFacade,
    private val tokenQueryFacade: TokenQueryFacade,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            extractTokenFromRequest(request)?.let { accessToken ->
                val validationResult =
                    tokenQueryFacade.validate().execute(
                        ValidateTokenUseCase.Query(accessToken),
                    )

                if (validationResult.isValid) {
                    validationResult.memberId?.let {
                        log.debug { "Valid JWT token found for member: $it" }

                        val credentials =
                            credentialsQueryFacade.getMemberCredentials().execute(
                                GetMemberCredentialsUseCase.Query(it),
                            )

                        val authentication =
                            UsernamePasswordAuthenticationToken(
                                credentials.memberId,
                                null,
                                listOf(SimpleGrantedAuthority(credentials.role.authority)),
                            ).apply {
                                details = WebAuthenticationDetailsSource().buildDetails(request)
                            }

                        SecurityContextHolder.getContext().authentication = authentication
                        log.debug { "Set authentication for member: $it with role: ${credentials.role}" }
                    }
                } else {
                    log.debug { "Invalid or expired JWT token" }
                }
            }
        } catch (e: Exception) {
            log.warn { "Authentication failed: ${e.message}" }
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }

    private fun extractTokenFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }
}
