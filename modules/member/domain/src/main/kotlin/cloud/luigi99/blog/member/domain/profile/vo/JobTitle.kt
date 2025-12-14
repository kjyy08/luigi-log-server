package cloud.luigi99.blog.member.domain.profile.vo

import cloud.luigi99.blog.common.domain.ValueObject

@JvmInline
value class JobTitle(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "JobTitle cannot be blank" }
        require(value.length <= 100) { "JobTitle cannot exceed 100 characters" }
    }
}
