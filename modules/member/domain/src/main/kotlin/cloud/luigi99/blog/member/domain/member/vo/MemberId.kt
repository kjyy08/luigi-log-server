package cloud.luigi99.blog.member.domain.member.vo

import cloud.luigi99.blog.common.domain.ValueObject
import java.io.Serializable
import java.util.UUID

@JvmInline
value class MemberId(val value: UUID) :
    ValueObject,
    Serializable {
    companion object {
        fun generate(): MemberId = MemberId(UUID.randomUUID())

        fun from(value: String): MemberId = MemberId(UUID.fromString(value))
    }

    override fun toString(): String = value.toString()
}
