package cloud.luigi99.blog.auth.token.adapter.`in`.web

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "AuthToken", description = "인증 토큰 관리 API")
interface AuthTokenApi {
    @Operation(
        summary = "토큰 갱신",
        description = "Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "토큰 갱신 성공",
                content = [Content(schema = Schema(hidden = true))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "유효하지 않은 Refresh Token",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "InvalidRefreshToken",
                                value = """
                        {
                          "success": false,
                          "data": null,
                          "error": {
                            "code": "AUTH_002",
                            "message": "유효하지 않은 토큰입니다."
                          },
                          "timestamp": "2025-12-05T10:00:00Z"
                        }
                        """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun reissue(
        @Parameter(
            description = "Refresh Token (Bearer {token})",
            `in` = ParameterIn.HEADER,
            name = "Authorization",
            required = true,
            example = "Bearer dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...",
        )
        refreshToken: String,
    ): ResponseEntity<Unit>

    @Operation(
        summary = "로그아웃",
        description = "현재 로그인된 사용자의 Refresh Token을 무효화하고 쿠키를 삭제합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "로그아웃 성공",
                content = [Content(schema = Schema(hidden = true))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 사용자",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "Unauthorized",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "AUTH_001",
                                    "message": "인증에 실패했습니다."
                                  },
                                  "timestamp": "2025-12-10T12:00:00Z"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "접근 권한 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "Forbidden",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "AUTH_003",
                                    "message": "접근 권한이 없습니다."
                                  },
                                  "timestamp": "2025-12-10T12:00:00Z"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun logout(
        @Parameter(hidden = true) memberId: String,
    ): ResponseEntity<Unit>
}
