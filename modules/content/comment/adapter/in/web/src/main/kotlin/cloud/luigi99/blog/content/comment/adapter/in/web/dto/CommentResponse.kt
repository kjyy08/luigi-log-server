package cloud.luigi99.blog.content.comment.adapter.`in`.web.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * 댓글 응답 DTO
 */
data class CommentResponse(
    @field:Schema(description = "댓글 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val commentId: String,
    @field:Schema(description = "게시글 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val postId: String?,
    @field:Schema(description = "작성자 정보")
    val author: AuthorResponse,
    @field:Schema(description = "댓글 내용", example = "좋은 글 감사합니다!")
    val content: String,
    @field:Schema(description = "생성 일시", example = "2025-01-01T12:00:00")
    val createdAt: LocalDateTime?,
    @field:Schema(description = "수정 일시", example = "2025-01-01T13:00:00")
    val updatedAt: LocalDateTime?,
)
