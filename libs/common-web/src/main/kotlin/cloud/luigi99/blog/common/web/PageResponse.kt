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
        /**
         * Spring Data Page 객체로부터 PageResponse 생성
         * @param page Spring Data Page 객체
         * @param oneBasedPage true인 경우 1-based 페이지 인덱스로 변환 (기본값: false)
         */
        fun <T> of(page: Page<T>, oneBasedPage: Boolean = false): PageResponse<T> {
            return PageResponse(
                content = page.content.toList(), // 방어적 복사
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                currentPage = if (oneBasedPage) page.number + 1 else page.number,
                size = page.size,
                first = page.isFirst,
                last = page.isLast,
                hasNext = page.hasNext(),
                hasPrevious = page.hasPrevious()
            )
        }

        /**
         * 직접 매개변수로 PageResponse 생성
         * @param content 페이지 내용
         * @param totalElements 전체 요소 수
         * @param currentPage 현재 페이지 번호
         * @param size 페이지 크기
         * @param oneBasedPage true인 경우 1-based 페이지 인덱스로 처리 (기본값: false)
         */
        fun <T> of(
            content: List<T>,
            totalElements: Long,
            currentPage: Int,
            size: Int,
            oneBasedPage: Boolean = false
        ): PageResponse<T> {
            // 입력 검증
            require(totalElements >= 0) { "totalElements는 0 이상이어야 합니다: $totalElements" }
            require(size >= 0) { "size는 0 이상이어야 합니다: $size" }

            val adjustedCurrentPage = if (oneBasedPage) {
                require(currentPage >= 1) { "oneBasedPage가 true일 때 currentPage는 1 이상이어야 합니다: $currentPage" }
                currentPage - 1 // 내부적으로는 0-based로 처리
            } else {
                require(currentPage >= 0) { "currentPage는 0 이상이어야 합니다: $currentPage" }
                currentPage
            }

            // 페이지 수 계산 (Spring Data와 동일한 로직)
            val totalPages = if (size == 0) {
                if (totalElements > 0) 1 else 0
            } else {
                ((totalElements + size - 1) / size).let { pages ->
                    // 오버플로우 방지
                    if (pages > Int.MAX_VALUE) Int.MAX_VALUE else pages.toInt()
                }
            }

            // 현재 페이지가 유효 범위를 벗어나는 경우 조정
            val validCurrentPage = when {
                totalPages == 0 -> 0
                adjustedCurrentPage >= totalPages -> totalPages - 1
                adjustedCurrentPage < 0 -> 0
                else -> adjustedCurrentPage
            }

            val isFirst = validCurrentPage == 0
            val isLast = totalPages == 0 || validCurrentPage >= totalPages - 1
            val hasNext = !isLast && totalPages > 0
            val hasPrevious = validCurrentPage > 0

            return PageResponse(
                content = content.toList(), // 방어적 복사
                totalElements = totalElements,
                totalPages = totalPages,
                currentPage = if (oneBasedPage) validCurrentPage + 1 else validCurrentPage,
                size = size,
                first = isFirst,
                last = isLast,
                hasNext = hasNext,
                hasPrevious = hasPrevious
            )
        }

        /**
         * 빈 PageResponse 생성
         */
        fun <T> empty(size: Int = 0, oneBasedPage: Boolean = false): PageResponse<T> {
            return of(
                content = emptyList(),
                totalElements = 0,
                currentPage = if (oneBasedPage) 1 else 0,
                size = size,
                oneBasedPage = oneBasedPage
            )
        }
    }
}