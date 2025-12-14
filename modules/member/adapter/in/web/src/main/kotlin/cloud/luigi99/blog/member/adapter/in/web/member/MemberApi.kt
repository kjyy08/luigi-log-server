package cloud.luigi99.blog.member.adapter.`in`.web.member

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.member.adapter.`in`.web.member.dto.MemberResponse
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

@Tag(name = "Member", description = "회원 정보 조회 API")
interface MemberApi {
    @Operation(
        summary = "현재 로그인한 회원 정보 조회",
        description = "JWT 토큰을 통해 인증된 현재 사용자의 기본 정보를 조회합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "회원 정보 조회 성공",
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
                          "timestamp": "2025-12-05T10:00:00Z"
                        }
                        """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "회원을 찾을 수 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "MemberNotFound",
                                value = """
                        {
                          "success": false,
                          "data": null,
                          "error": {
                            "code": "MEMBER_001",
                            "message": "회원을 찾을 수 없습니다."
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
    fun getCurrentMember(
        @AuthenticationPrincipal memberId: String,
    ): ResponseEntity<CommonResponse<MemberResponse>>

    @Operation(
        summary = "회원 탈퇴",
        description = "현재 로그인한 회원의 계정을 탈퇴합니다. 회원과 관련된 모든 데이터가 삭제됩니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "회원 탈퇴 성공",
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
                responseCode = "404",
                description = "회원을 찾을 수 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "MemberNotFound",
                                value = """
                        {
                          "success": false,
                          "data": null,
                          "error": {
                            "code": "MEMBER_001",
                            "message": "회원을 찾을 수 없습니다."
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
    fun deleteMember(
        @AuthenticationPrincipal memberId: String,
    ): ResponseEntity<Unit>
}
