package cloud.luigi99.blog.member.domain.profile.vo

import cloud.luigi99.blog.common.domain.ValueObject

@JvmInline
value class ContactEmail(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Contact email cannot be blank" }
        require(value.matches(EMAIL_PATTERN)) { "Invalid email format: $value" }
        require(value.length <= 255) { "Email cannot exceed 255 characters" }
    }

    companion object {
        private val EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    }
}
