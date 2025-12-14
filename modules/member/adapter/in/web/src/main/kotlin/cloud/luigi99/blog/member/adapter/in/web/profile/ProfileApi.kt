package cloud.luigi99.blog.member.adapter.`in`.web.profile

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.member.adapter.`in`.web.profile.dto.ProfileResponse
import cloud.luigi99.blog.member.adapter.`in`.web.profile.dto.UpdateProfileRequest
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
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "Profile", description = "프로필 관리 API")
interface ProfileApi {
    @Operation(
        summary = "프로필 조회",
        description = "사용자의 프로필 정보를 조회합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "프로필 조회 성공",
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
                description = "프로필을 찾을 수 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "ProfileNotFound",
                                value = """
                        {
                          "success": false,
                          "data": null,
                          "error": {
                            "code": "PROFILE_001",
                            "message": "프로필을 찾을 수 없습니다."
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
    fun getProfile(
        @AuthenticationPrincipal memberId: String,
    ): ResponseEntity<CommonResponse<ProfileResponse>>

    @Operation(
        summary = "프로필 수정",
        description = "현재 로그인한 사용자의 프로필 정보를 수정합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "프로필 수정 성공",
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 데이터",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "BadRequest",
                                value = """
                        {
                          "success": false,
                          "data": null,
                          "error": {
                            "code": "COMMON_001",
                            "message": "잘못된 요청입니다."
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
                description = "프로필을 찾을 수 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "ProfileNotFound",
                                value = """
                        {
                          "success": false,
                          "data": null,
                          "error": {
                            "code": "PROFILE_001",
                            "message": "프로필을 찾을 수 없습니다."
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
    fun updateProfile(
        @AuthenticationPrincipal memberId: String,
        @RequestBody request: UpdateProfileRequest,
    ): ResponseEntity<CommonResponse<ProfileResponse>>
}
