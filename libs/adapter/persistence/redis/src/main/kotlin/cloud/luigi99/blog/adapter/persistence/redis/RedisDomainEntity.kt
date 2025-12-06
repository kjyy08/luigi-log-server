package cloud.luigi99.blog.adapter.persistence.redis

import cloud.luigi99.blog.common.domain.DomainEntity
import cloud.luigi99.blog.common.domain.ValueObject
import org.springframework.data.redis.core.TimeToLive
import java.time.LocalDateTime

abstract class RedisDomainEntity<ID : ValueObject> : DomainEntity<ID>() {
    @TimeToLive
    var ttl: Long? = null

    override var createdAt: LocalDateTime? = null
    override var updatedAt: LocalDateTime? = null

    fun getId(): ID = entityId
}
