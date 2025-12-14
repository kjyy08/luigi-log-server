package cloud.luigi99.blog.auth.credentials.adapter.`in`.web

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.auth.credentials.adapter.`in`.web.dto.CredentialsResponse
import cloud.luigi99.blog.auth.credentials.adapter.`in`.web.dto.UpdateCredentialsRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "MemberCredentials", description = "회원 인증 정보 관리 API")
interface MemberCredentialsApi {
    @Operation(
        summary = "현재 로그인한 회원 인증 정보 조회",
        description = "JWT 토큰을 통해 인증된 현재 사용자의 인증 정보(OAuth 제공자, 권한 등)를 조회합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "인증 정보 조회 성공",
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
                responseCode = "404",
                description = "인증 정보를 찾을 수 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "CredentialsNotFound",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "CREDENTIALS_001",
                                    "message": "인증 정보를 찾을 수 없습니다."
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
    fun getMemberCredentials(
        @AuthenticationPrincipal memberId: String,
    ): ResponseEntity<CommonResponse<CredentialsResponse>>

    @Operation(
        summary = "사용자 인증 정보 업데이트",
        description = "회원의 권한을 변경합니다. ADMIN 권한이 필요합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "인증 정보 업데이트 성공",
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
                description = "권한 없음 (ADMIN 권한 필요)",
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
                                    "code": "AUTH_002",
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
            ApiResponse(
                responseCode = "404",
                description = "인증 정보를 찾을 수 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "CredentialsNotFound",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "CREDENTIALS_001",
                                    "message": "인증 정보를 찾을 수 없습니다."
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
    fun updateCredentials(
        @PathVariable memberId: String,
        @RequestBody request: UpdateCredentialsRequest,
    ): ResponseEntity<CommonResponse<Unit>>
}
