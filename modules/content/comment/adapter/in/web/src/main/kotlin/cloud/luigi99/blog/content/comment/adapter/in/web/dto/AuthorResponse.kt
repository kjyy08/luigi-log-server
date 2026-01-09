package cloud.luigi99.blog.content.comment.adapter.`in`.web.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 댓글 작성자 정보 DTO
 */
data class AuthorResponse(
    @field:Schema(description = "회원 ID", example = "987e6543-e21b-98d7-a654-426614174111")
    val memberId: String,
    @field:Schema(description = "닉네임", example = "Luigi99")
    val nickname: String,
    @field:Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    val profileImageUrl: String?,
    @field:Schema(description = "사용자 이름", example = "luigi99")
    val username: String,
)
