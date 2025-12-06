package cloud.luigi99.blog.common.domain

import java.time.LocalDateTime

abstract class DomainEntity<out T : ValueObject>(
    open var createdAt: LocalDateTime? = null,
    open var updatedAt: LocalDateTime? = null,
) {
    abstract val entityId: T

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DomainEntity<*>

        return entityId == other.entityId
    }

    override fun hashCode(): Int = entityId.hashCode()
}
