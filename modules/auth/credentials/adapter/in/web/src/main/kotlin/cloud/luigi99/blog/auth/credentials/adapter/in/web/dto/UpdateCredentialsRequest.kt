package cloud.luigi99.blog.auth.credentials.adapter.`in`.web.dto

import cloud.luigi99.blog.auth.credentials.domain.enums.Role
import io.swagger.v3.oas.annotations.media.Schema

data class UpdateCredentialsRequest(
    @param:Schema(description = "회원 권한", example = "ADMIN", required = true)
    val role: Role,
)
