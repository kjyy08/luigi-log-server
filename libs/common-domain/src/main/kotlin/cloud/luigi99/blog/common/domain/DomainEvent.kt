package cloud.luigi99.blog.common.domain

import java.time.LocalDateTime
import java.util.*

interface DomainEvent {
    val eventId: UUID
    val occurredAt: LocalDateTime
    val aggregateId: UUID
    val eventType: String
}