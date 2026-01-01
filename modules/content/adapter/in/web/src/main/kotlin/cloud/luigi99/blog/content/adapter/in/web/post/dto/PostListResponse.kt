package cloud.luigi99.blog.content.adapter.`in`.web.post.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 블로그 글 목록 응답 DTO
 */
data class PostListResponse(
    @field:Schema(description = "글 목록")
    val posts: List<PostSummaryResponse>,
    @field:Schema(description = "총 개수", example = "42")
    val total: Int,
)
