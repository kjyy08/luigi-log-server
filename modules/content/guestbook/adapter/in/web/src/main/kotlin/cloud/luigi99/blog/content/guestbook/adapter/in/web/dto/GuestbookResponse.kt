package cloud.luigi99.blog.content.guestbook.adapter.`in`.web.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 방명록 응답 DTO
 */
data class GuestbookResponse(
    @field:Schema(description = "방명록 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val guestbookId: String,
    @field:Schema(description = "작성자 정보")
    val author: AuthorResponse,
    @field:Schema(description = "방명록 내용", example = "안녕하세요! 블로그 잘 보고 있습니다.")
    val content: String,
    @field:Schema(description = "생성일시", example = "2024-01-09T12:00:00")
    val createdAt: String,
    @field:Schema(description = "수정일시", example = "2024-01-09T12:00:00")
    val updatedAt: String,
)
