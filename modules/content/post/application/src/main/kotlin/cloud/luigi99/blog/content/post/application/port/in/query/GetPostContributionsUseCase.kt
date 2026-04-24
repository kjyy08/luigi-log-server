package cloud.luigi99.blog.content.post.application.port.`in`.query

import java.time.LocalDate

interface GetPostContributionsUseCase {
    fun execute(query: Query): Response

    data class Query(val from: LocalDate? = null, val to: LocalDate? = null, val type: String? = null)

    data class Response(
        val from: String,
        val to: String,
        val totalCount: Int,
        val days: List<Day>,
    )

    data class Day(val date: String, val count: Int)
}
