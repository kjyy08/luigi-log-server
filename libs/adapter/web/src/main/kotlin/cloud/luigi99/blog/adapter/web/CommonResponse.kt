package cloud.luigi99.blog.adapter.web

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "공통 응답 객체")
data class CommonResponse<T>(
    @Schema(description = "성공 여부", example = "true")
    val success: Boolean,
    @Schema(description = "응답 데이터")
    val data: T? = null,
    @Schema(description = "에러 정보")
    val error: ErrorDetail? = null,
    @Schema(description = "응답 시간")
    val timestamp: Instant = Instant.now(),
) {
    companion object {
        fun <T> success(data: T): CommonResponse<T> = CommonResponse(success = true, data = data)

        fun <T> error(code: String, message: String): CommonResponse<T> =
            CommonResponse(
                success = false,
                error = ErrorDetail(code, message),
            )
    }

    @Schema(description = "에러 상세 정보")
    data class ErrorDetail(
        @Schema(description = "에러 코드", example = "COMMON_001")
        val code: String,
        @Schema(description = "에러 메시지", example = "잘못된 요청입니다.")
        val message: String,
    )
}
