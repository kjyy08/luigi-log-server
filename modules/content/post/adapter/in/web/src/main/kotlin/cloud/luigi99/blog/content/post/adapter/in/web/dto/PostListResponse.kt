package cloud.luigi99.blog.content.post.adapter.`in`.web.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 블로그 글 목록 응답 DTO
 */
data class PostListResponse(
    @field:Schema(description = "글 목록")
    val posts: List<PostSummaryResponse>,
    @field:Schema(description = "총 개수", example = "42")
    val total: Int,
    @field:Schema(description = "페이지 정보")
    val pageInfo: PageInfoResponse = PageInfoResponse(limit = posts.size, hasNext = false, nextCursor = null),
)

data class PageInfoResponse(
    @field:Schema(description = "요청/적용된 페이지 크기", example = "20")
    val limit: Int,
    @field:Schema(description = "다음 페이지 존재 여부", example = "true")
    val hasNext: Boolean,
    @field:Schema(description = "다음 페이지 커서")
    val nextCursor: String?,
)
