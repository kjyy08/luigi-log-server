package cloud.luigi99.blog.content.adapter.`in`.web.post.dto

import io.swagger.v3.oas.annotations.media.Schema

data class AuthorResponse(
    @field:Schema(description = "작성자 ID", example = "987e6543-e21b-98d7-a654-426614174111")
    val memberId: String,
    @field:Schema(description = "작성자 닉네임", example = "Luigi99")
    val nickname: String,
    @field:Schema(description = "작성자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    val profileImageUrl: String?,
)
