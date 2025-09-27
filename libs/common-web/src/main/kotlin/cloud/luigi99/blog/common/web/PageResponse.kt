package cloud.luigi99.blog.common.web

import org.springframework.data.domain.Page

data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    companion object {
        fun <T> of(page: Page<T>): PageResponse<T> {
            return PageResponse(
                content = page.content,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                currentPage = page.number,
                size = page.size,
                first = page.isFirst,
                last = page.isLast,
                hasNext = page.hasNext(),
                hasPrevious = page.hasPrevious()
            )
        }

        fun <T> of(
            content: List<T>,
            totalElements: Long,
            currentPage: Int,
            size: Int
        ): PageResponse<T> {
            val totalPages = if (size == 0) 0 else ((totalElements + size - 1) / size).toInt()
            return PageResponse(
                content = content,
                totalElements = totalElements,
                totalPages = totalPages,
                currentPage = currentPage,
                size = size,
                first = currentPage == 0,
                last = currentPage >= totalPages - 1,
                hasNext = currentPage < totalPages - 1,
                hasPrevious = currentPage > 0
            )
        }
    }
}