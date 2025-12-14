package cloud.luigi99.blog.member.adapter.`in`.web.member.dto

import io.swagger.v3.oas.annotations.media.Schema

data class MemberResponse(
    @param:Schema(description = "회원 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val memberId: String,
    @param:Schema(description = "이메일", example = "user@example.com")
    val email: String,
    @param:Schema(description = "사용자 이름", example = "Luigi99")
    val username: String,
)
