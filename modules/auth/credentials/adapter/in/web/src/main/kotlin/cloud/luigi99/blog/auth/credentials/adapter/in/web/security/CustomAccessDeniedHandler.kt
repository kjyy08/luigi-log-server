package cloud.luigi99.blog.auth.credentials.adapter.`in`.web.security

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.common.exception.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper
import java.time.Instant

@Component
class CustomAccessDeniedHandler(private val jsonMapper: JsonMapper) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        val errorResponse =
            CommonResponse
                .error<Unit>(
                    ErrorCode.ACCESS_DENIED.code,
                    ErrorCode.ACCESS_DENIED.message,
                ).copy(timestamp = Instant.now())

        response.status = HttpStatus.FORBIDDEN.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        response.writer.write(jsonMapper.writeValueAsString(errorResponse))
    }
}
