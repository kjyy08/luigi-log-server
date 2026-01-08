package cloud.luigi99.blog.member.domain.profile.vo

import cloud.luigi99.blog.common.domain.ValueObject

/**
 * 거주지 또는 근무지
 *
 * 사용자의 거주지 또는 근무지 위치를 나타내는 Value Object
 */
@JvmInline
value class Location(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Location cannot be blank" }
        require(value.length <= 100) { "Location cannot exceed 100 characters" }
    }
}
