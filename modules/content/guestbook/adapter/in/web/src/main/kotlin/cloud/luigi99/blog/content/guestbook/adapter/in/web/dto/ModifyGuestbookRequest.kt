package cloud.luigi99.blog.content.guestbook.adapter.`in`.web.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 방명록 수정 요청 DTO
 */
data class ModifyGuestbookRequest(
    @field:Schema(description = "수정할 방명록 내용", example = "수정된 내용입니다.")
    val content: String,
)
