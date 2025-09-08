package cloud.luigi99.blog.common.persistence

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseJpaEntity {
    @Id
    @Column(columnDefinition = "uuid")
    open val id: UUID = UUID.randomUUID()

    @CreatedDate
    @Column(nullable = false, updatable = false)
    open var createdAt: LocalDateTime = LocalDateTime.now()
        protected set

    @LastModifiedDate
    @Column(nullable = false)
    open var updatedAt: LocalDateTime? = null
        protected set

    @Version
    @Column(nullable = false)
    open var version: Long = 0L
        protected set
}