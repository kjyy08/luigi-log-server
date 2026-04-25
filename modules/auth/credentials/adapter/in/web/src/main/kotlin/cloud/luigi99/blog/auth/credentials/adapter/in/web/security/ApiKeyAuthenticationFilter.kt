package cloud.luigi99.blog.auth.credentials.adapter.`in`.web.security

import cloud.luigi99.blog.auth.credentials.application.port.`in`.query.ApiKeyQueryFacade
import cloud.luigi99.blog.auth.credentials.application.port.`in`.query.AuthenticateApiKeyUseCase
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ApiKeyAuthenticationFilter(
    private val apiKeyQueryFacade: ApiKeyQueryFacade,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (SecurityContextHolder.getContext().authentication == null) {
            extractApiKey(request)?.let { apiKey ->
                val result =
                    apiKeyQueryFacade.authenticateApiKey().execute(
                        AuthenticateApiKeyUseCase.Query(
                            secretKey = apiKey,
                            path = request.requestURI,
                        ),
                    )

                if (result != null) {
                    val authentication =
                        UsernamePasswordAuthenticationToken(
                            result.ownerMemberId,
                            null,
                            result.authorities.map { SimpleGrantedAuthority(it) },
                        ).apply {
                            details = WebAuthenticationDetailsSource().buildDetails(request)
                        }

                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun extractApiKey(request: HttpServletRequest): String? {
        val bearerToken =
            request
                .getHeader("Authorization")
                ?.takeIf { it.startsWith("Bearer ") }
                ?.removePrefix("Bearer ")
        return bearerToken?.takeIf { it.isNotBlank() }
            ?: request.getHeader("X-API-Key")?.takeIf { it.isNotBlank() }
    }
}
