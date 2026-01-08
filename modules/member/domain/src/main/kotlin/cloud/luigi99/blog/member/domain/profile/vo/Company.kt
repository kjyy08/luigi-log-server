package cloud.luigi99.blog.member.domain.profile.vo

import cloud.luigi99.blog.common.domain.ValueObject

/**
 * 소속 회사 또는 조직
 *
 * 사용자가 현재 소속되어 있는 회사나 조직명을 나타내는 Value Object
 */
@JvmInline
value class Company(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Company cannot be blank" }
        require(value.length <= 100) { "Company cannot exceed 100 characters" }
    }
}
