package cloud.luigi99.blog.content.adapter.`in`.web.post.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * 블로그 글 목록 조회 응답 DTO
 */
data class PostSummaryResponse(
    @field:Schema(description = "Post ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val postId: String,
    @field:Schema(description = "작성자 Member ID", example = "987e6543-e21b-98d7-a654-426614174111")
    val memberId: String,
    @field:Schema(description = "제목", example = "Kotlin으로 DDD 구현하기")
    val title: String,
    @field:Schema(description = "URL slug", example = "kotlin-ddd-implementation")
    val slug: String,
    @field:Schema(description = "콘텐츠 타입", example = "BLOG")
    val type: String,
    @field:Schema(description = "상태", example = "PUBLISHED")
    val status: String,
    @field:Schema(description = "태그 목록", example = "[\"Kotlin\", \"DDD\"]")
    val tags: Set<String>,
    @field:Schema(description = "생성 시간", example = "2025-01-01T12:00:00")
    val createdAt: LocalDateTime?,
)
