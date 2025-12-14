package cloud.luigi99.blog.member.domain.profile.vo

import cloud.luigi99.blog.common.domain.ValueObject

@JvmInline
value class Nickname(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Nickname cannot be blank" }
        require(value.length <= 100) { "Nickname cannot exceed 100 characters" }
    }
}
