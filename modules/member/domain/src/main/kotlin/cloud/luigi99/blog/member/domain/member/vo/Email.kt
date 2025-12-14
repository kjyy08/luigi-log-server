package cloud.luigi99.blog.member.domain.member.vo

import cloud.luigi99.blog.common.domain.ValueObject

@JvmInline
value class Email(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Email cannot be blank" }
        require(value.matches(EMAIL_REGEX)) { "Invalid email format: $value" }
    }

    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    }

    override fun toString(): String = value
}
