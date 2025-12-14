package cloud.luigi99.blog.auth.credentials.adapter.`in`.web.security

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.common.exception.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper
import java.time.Instant

@Component
class CustomAuthenticationEntryPoint(private val jsonMapper: JsonMapper) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        val errorResponse =
            CommonResponse
                .error<Unit>(
                    ErrorCode.UNAUTHORIZED.code,
                    ErrorCode.UNAUTHORIZED.message,
                ).copy(timestamp = Instant.now())

        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        response.writer.write(jsonMapper.writeValueAsString(errorResponse))
    }
}
