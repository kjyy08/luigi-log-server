package cloud.luigi99.blog.content.post.adapter.`in`.web.dto

import io.swagger.v3.oas.annotations.media.Schema

data class PostContributionsResponse(
    @field:Schema(description = "조회 시작일", example = "2025-01-01")
    val from: String,
    @field:Schema(description = "조회 종료일", example = "2025-12-31")
    val to: String,
    @field:Schema(description = "기간 내 발행 글 총 수", example = "42")
    val totalCount: Int,
    @field:Schema(description = "날짜별 발행 글 수")
    val days: List<PostContributionDayResponse>,
)

data class PostContributionDayResponse(
    @field:Schema(description = "날짜", example = "2025-01-01")
    val date: String,
    @field:Schema(description = "발행 글 수", example = "1")
    val count: Int,
)
