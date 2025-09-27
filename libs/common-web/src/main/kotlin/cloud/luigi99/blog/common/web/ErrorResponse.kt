package cloud.luigi99.blog.common.web

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class ErrorResponse(
    val error: String,
    val message: String,
    val path: String? = null,
    val details: List<String>? = null,
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun of(
            error: String,
            message: String,
            path: String? = null
        ): ErrorResponse {
            return ErrorResponse(
                error = error,
                message = message,
                path = path
            )
        }

        fun of(
            error: String,
            message: String,
            path: String? = null,
            details: List<String>
        ): ErrorResponse {
            return ErrorResponse(
                error = error,
                message = message,
                path = path,
                details = details
            )
        }
    }
}