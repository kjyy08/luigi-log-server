package cloud.luigi99.blog.member.domain.profile.vo

import cloud.luigi99.blog.common.domain.ValueObject
import java.io.Serializable
import java.util.UUID

@JvmInline
value class ProfileId(val value: UUID) :
    ValueObject,
    Serializable {
    companion object {
        fun generate(): ProfileId = ProfileId(UUID.randomUUID())

        fun from(value: String): ProfileId = ProfileId(UUID.fromString(value))
    }

    override fun toString(): String = value.toString()
}
