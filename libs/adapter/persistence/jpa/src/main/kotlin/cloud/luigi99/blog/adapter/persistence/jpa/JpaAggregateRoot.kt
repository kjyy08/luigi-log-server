package cloud.luigi99.blog.adapter.persistence.jpa

import cloud.luigi99.blog.common.domain.AggregateRoot
import cloud.luigi99.blog.common.domain.ValueObject
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class JpaAggregateRoot<ID : ValueObject> :
    AggregateRoot<ID>(),
    Persistable<ID> {
    @CreatedDate
    @Column(nullable = false, updatable = false)
    override var createdAt: LocalDateTime? = null

    @LastModifiedDate
    @Column(nullable = false)
    override var updatedAt: LocalDateTime? = null

    override fun isNew(): Boolean = createdAt == null

    override fun getId(): ID = entityId
}
