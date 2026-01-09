package cloud.luigi99.blog.content.comment.adapter.`in`.web.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 댓글 수정 요청 DTO
 */
data class UpdateCommentRequest(
    @field:Schema(description = "댓글 내용", example = "수정된 댓글입니다.")
    val content: String,
)
