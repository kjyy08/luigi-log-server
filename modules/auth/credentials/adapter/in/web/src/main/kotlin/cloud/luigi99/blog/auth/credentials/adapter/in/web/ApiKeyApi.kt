package cloud.luigi99.blog.auth.credentials.adapter.`in`.web

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.auth.credentials.adapter.`in`.web.dto.ApiKeyListResponse
import cloud.luigi99.blog.auth.credentials.adapter.`in`.web.dto.CreateApiKeyRequest
import cloud.luigi99.blog.auth.credentials.adapter.`in`.web.dto.CreateApiKeyResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@Tag(name = "ApiKey", description = "API Key 관리 API")
interface ApiKeyApi {
    @Operation(
        summary = "API Key 생성",
        description = "현재 로그인한 ADMIN 사용자의 API Key를 생성합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "API Key 생성 성공"),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN 권한 필요)"),
        ],
    )
    fun createApiKey(
        @AuthenticationPrincipal memberId: String,
        @RequestBody request: CreateApiKeyRequest,
    ): ResponseEntity<CommonResponse<CreateApiKeyResponse>>

    @Operation(
        summary = "API Key 목록 조회",
        description = "현재 로그인한 ADMIN 사용자의 API Key 목록을 조회합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "API Key 목록 조회 성공"),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN 권한 필요)"),
        ],
    )
    fun listApiKeys(
        @AuthenticationPrincipal memberId: String,
    ): ResponseEntity<CommonResponse<ApiKeyListResponse>>

    @Operation(
        summary = "API Key 폐기",
        description = "현재 로그인한 ADMIN 사용자의 API Key를 폐기합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "API Key 폐기 성공"),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN 권한 필요)"),
            ApiResponse(responseCode = "404", description = "API Key를 찾을 수 없음"),
        ],
    )
    fun revokeApiKey(
        @AuthenticationPrincipal memberId: String,
        @PathVariable apiKeyId: UUID,
    ): ResponseEntity<Unit>
}
