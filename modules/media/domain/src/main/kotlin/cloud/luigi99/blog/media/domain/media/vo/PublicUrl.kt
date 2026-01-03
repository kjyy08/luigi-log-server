package cloud.luigi99.blog.media.domain.media.vo

import cloud.luigi99.blog.common.domain.ValueObject

/**
 * 공개 URL Value Object
 *
 * 파일에 접근 가능한 공개 URL을 나타냅니다.
 */
@JvmInline
value class PublicUrl(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Public URL cannot be blank" }
    }
}
