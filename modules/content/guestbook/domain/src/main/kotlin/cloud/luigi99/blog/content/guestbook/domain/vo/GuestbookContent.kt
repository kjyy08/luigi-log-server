package cloud.luigi99.blog.content.guestbook.domain.vo

import cloud.luigi99.blog.common.domain.ValueObject

/**
 * 방명록 내용 값 객체
 *
 * 방명록에 작성되는 내용을 표현하며, 유효성 검증을 수행합니다.
 */
@JvmInline
value class GuestbookContent(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "방명록 내용은 비어있을 수 없습니다" }
        require(value.length <= MAX_LENGTH) {
            "방명록 내용은 ${MAX_LENGTH}자를 초과할 수 없습니다 (현재: ${value.length}자)"
        }
    }

    companion object {
        const val MAX_LENGTH = 5000
    }
}
