package cloud.luigi99.blog.member.domain.profile.vo

import cloud.luigi99.blog.common.domain.ValueObject

@JvmInline
value class Url(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "URL cannot be blank" }
        require(value.matches(URL_PATTERN)) { "Invalid URL format: $value" }
        require(value.length <= 2000) { "URL cannot exceed 2000 characters" }
    }

    companion object {
        private val URL_PATTERN = "^https?://[^\\s]+$".toRegex()
    }
}
