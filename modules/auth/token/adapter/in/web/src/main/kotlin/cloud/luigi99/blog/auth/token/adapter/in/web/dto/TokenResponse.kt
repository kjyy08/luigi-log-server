package cloud.luigi99.blog.auth.token.adapter.`in`.web.dto

import io.swagger.v3.oas.annotations.media.Schema

data class TokenResponse(
    @field:Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    val accessToken: String,
    @field:Schema(description = "리프레시 토큰", example = "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...")
    val refreshToken: String,
)
