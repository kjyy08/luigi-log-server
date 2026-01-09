package cloud.luigi99.blog.content.guestbook.adapter.`in`.web.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 방명록 작성 요청 DTO
 */
data class CreateGuestbookRequest(
    @field:Schema(description = "방명록 내용", example = "안녕하세요! 블로그 잘 보고 있습니다.")
    val content: String,
)
