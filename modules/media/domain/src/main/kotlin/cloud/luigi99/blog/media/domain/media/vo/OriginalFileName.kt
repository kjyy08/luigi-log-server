package cloud.luigi99.blog.media.domain.media.vo

import cloud.luigi99.blog.common.domain.ValueObject

/**
 * 원본 파일명 Value Object
 *
 * 업로드된 파일의 원본 이름을 나타냅니다.
 */
@JvmInline
value class OriginalFileName(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Original file name cannot be blank" }
        require(value.length <= 255) { "File name too long" }
    }
}
