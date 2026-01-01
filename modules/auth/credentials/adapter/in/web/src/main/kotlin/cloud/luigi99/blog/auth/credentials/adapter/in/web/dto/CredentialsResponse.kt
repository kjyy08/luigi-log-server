package cloud.luigi99.blog.auth.credentials.adapter.`in`.web.dto

import cloud.luigi99.blog.auth.credentials.domain.enums.Role
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class CredentialsResponse(
    @field:Schema(description = "인증 정보 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val credentialsId: String,
    @field:Schema(description = "회원 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val memberId: String,
    @field:Schema(description = "OAuth 제공자", example = "GITHUB")
    val oauthProvider: String,
    @field:Schema(description = "OAuth 제공자 ID", example = "12345678")
    val oauthProviderId: String,
    @field:Schema(description = "회원 권한", example = "USER")
    val role: Role,
    @field:Schema(description = "마지막 로그인 시간", example = "2025-12-10T12:00:00")
    val lastLoginAt: LocalDateTime?,
)
