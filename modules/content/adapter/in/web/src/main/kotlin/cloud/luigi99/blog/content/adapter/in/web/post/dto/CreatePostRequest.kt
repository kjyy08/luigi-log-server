package cloud.luigi99.blog.content.adapter.`in`.web.post.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 블로그 글 생성 요청 DTO
 */
data class CreatePostRequest(
    @field:Schema(description = "제목", example = "Kotlin으로 DDD 구현하기")
    val title: String,
    @field:Schema(description = "URL slug", example = "kotlin-ddd-implementation")
    val slug: String,
    @field:Schema(description = "본문 (Markdown)", example = "# 시작하기\n\nKotlin과 DDD를 결합하면...")
    val body: String,
    @field:Schema(description = "콘텐츠 타입", example = "BLOG", allowableValues = ["BLOG", "PORTFOLIO"])
    val type: String,
    @field:Schema(description = "태그 목록", example = "[\"Kotlin\", \"DDD\", \"Spring\"]")
    val tags: List<String>? = null,
)
