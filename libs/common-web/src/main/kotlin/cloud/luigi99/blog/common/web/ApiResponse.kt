package cloud.luigi99.blog.common.web

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String = "",
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun <T> success(data: T, message: String = "성공"): ApiResponse<T> {
            return ApiResponse(
                success = true,
                data = data,
                message = message
            )
        }

        fun <T> success(message: String = "성공"): ApiResponse<T> {
            return ApiResponse(
                success = true,
                data = null,
                message = message
            )
        }

        fun <T> failure(message: String): ApiResponse<T> {
            return ApiResponse(
                success = false,
                data = null,
                message = message
            )
        }

        fun <T> error(message: String): ApiResponse<T> {
            return ApiResponse(
                success = false,
                data = null,
                message = message
            )
        }
    }
}