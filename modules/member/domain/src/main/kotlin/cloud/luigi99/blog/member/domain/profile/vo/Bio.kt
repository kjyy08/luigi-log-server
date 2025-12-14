package cloud.luigi99.blog.member.domain.profile.vo

import cloud.luigi99.blog.common.domain.ValueObject

@JvmInline
value class Bio(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Bio cannot be blank" }
        require(value.length <= 500) { "Bio cannot exceed 500 characters" }
    }
}
