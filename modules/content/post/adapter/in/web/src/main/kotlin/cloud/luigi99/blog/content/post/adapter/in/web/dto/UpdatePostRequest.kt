package cloud.luigi99.blog.content.post.adapter.`in`.web.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 블로그 글 수정 요청 DTO
 *
 * 모든 필드가 선택적입니다. null인 필드는 변경되지 않습니다.
 * tags는 null이면 변경하지 않고, 빈 배열이면 전체 제거하며, 값이 있으면 요청 태그로 교체합니다.
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
    @field:Schema(description = "태그 목록 (null이면 변경하지 않음, []이면 전체 제거)", example = "[\"Kotlin\", \"DDD\"]")
    val tags: List<String>? = null,
)
