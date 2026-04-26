package cloud.luigi99.blog.auth.credentials.domain.vo

import cloud.luigi99.blog.common.domain.ValueObject
import java.io.Serializable
import java.util.UUID

@JvmInline
value class ApiKeyId(val value: UUID) :
    ValueObject,
    Serializable {
    companion object {
        fun generate(): ApiKeyId = ApiKeyId(UUID.randomUUID())

        fun from(value: String): ApiKeyId = ApiKeyId(UUID.fromString(value))
    }

    override fun toString(): String = value.toString()
}
