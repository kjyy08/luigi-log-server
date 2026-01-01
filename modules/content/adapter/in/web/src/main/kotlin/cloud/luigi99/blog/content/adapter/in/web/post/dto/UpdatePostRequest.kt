package cloud.luigi99.blog.content.adapter.`in`.web.post.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 블로그 글 수정 요청 DTO
 *
 * 모든 필드가 선택적입니다. null인 필드는 변경되지 않습니다.
 */
data class UpdatePostRequest(
    @field:Schema(description = "제목 (null이면 변경하지 않음)", example = "수정된 제목")
    val title: String? = null,
    @field:Schema(description = "본문 (null이면 변경하지 않음)", example = "# 수정된 본문\n\n...")
    val body: String? = null,
    @field:Schema(
        description = "상태 (null이면 변경하지 않음)",
        example = "PUBLISHED",
        allowableValues = ["DRAFT", "PUBLISHED", "ARCHIVED"],
    )
    val status: String? = null,
)
