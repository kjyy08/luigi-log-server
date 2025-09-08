package cloud.luigi99.blog.common.domain

import java.time.LocalDateTime
import java.util.*

abstract class BaseEntity {
    abstract val id: UUID
    abstract val createdAt: LocalDateTime
    abstract val updatedAt: LocalDateTime?
}