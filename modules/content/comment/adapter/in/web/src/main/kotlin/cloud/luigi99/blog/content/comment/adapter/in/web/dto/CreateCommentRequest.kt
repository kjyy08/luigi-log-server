package cloud.luigi99.blog.content.comment.adapter.`in`.web.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 댓글 생성 요청 DTO
 */
data class CreateCommentRequest(
    @field:Schema(description = "게시글 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val postId: String,
    @field:Schema(description = "댓글 내용", example = "좋은 글 감사합니다!")
    val content: String,
)
