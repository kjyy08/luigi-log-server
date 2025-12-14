package cloud.luigi99.blog.auth.token.domain.vo

import cloud.luigi99.blog.common.domain.ValueObject
import java.io.Serializable
import java.util.UUID

@JvmInline
value class TokenId(val value: UUID) :
    ValueObject,
    Serializable {
    companion object {
        fun generate(): TokenId = TokenId(UUID.randomUUID())

        fun from(value: String): TokenId = TokenId(UUID.fromString(value))
    }

    override fun toString(): String = value.toString()
}
