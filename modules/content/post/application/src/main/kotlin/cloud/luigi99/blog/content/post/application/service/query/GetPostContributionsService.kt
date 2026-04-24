package cloud.luigi99.blog.content.post.application.service.query

import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode
import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostContributionsUseCase
import cloud.luigi99.blog.content.post.application.port.out.PostRepository
import cloud.luigi99.blog.content.post.domain.vo.ContentType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class GetPostContributionsService(private val postRepository: PostRepository) : GetPostContributionsUseCase {
    @Transactional(readOnly = true)
    override fun execute(query: GetPostContributionsUseCase.Query): GetPostContributionsUseCase.Response {
        val to = query.to ?: LocalDate.now()
        val from = query.from ?: to.minusDays(364)
        if (from.isAfter(to)) {
            throw InvalidPostContributionsQueryException("from must be before or equal to to")
        }

        val type = query.type?.let { parseContentType(it) }
        val counts = postRepository.contributions(from, to, type).associate { it.date to it.count }
        val days =
            generateSequence(from) { date -> date.plusDays(1).takeIf { !it.isAfter(to) } }
                .map { date -> GetPostContributionsUseCase.Day(date.toString(), counts[date] ?: 0) }
                .toList()

        return GetPostContributionsUseCase.Response(
            from = from.toString(),
            to = to.toString(),
            totalCount = days.sumOf { it.count },
            days = days,
        )
    }

    private fun parseContentType(value: String): ContentType =
        try {
            ContentType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            throw InvalidPostContributionsQueryException("Invalid type: $value", e)
        }
}

private class InvalidPostContributionsQueryException(message: String, cause: Throwable? = null) :
    BusinessException(ErrorCode.INVALID_INPUT, message) {
    init {
        if (cause != null) initCause(cause)
    }
}
