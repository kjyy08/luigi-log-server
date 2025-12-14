package cloud.luigi99.blog.member.domain.member.vo

import cloud.luigi99.blog.common.domain.ValueObject

@JvmInline
value class Username(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Username cannot be blank" }
        require(value.length in 2..100) {
            "Username must be between 2 and 100 characters (actual: ${value.length})"
        }
    }

    override fun toString(): String = value
}
