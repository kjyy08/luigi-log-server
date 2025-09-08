package cloud.luigi99.blog.common.messaging

import cloud.luigi99.blog.common.domain.DomainEvent

interface EventPublisher {
    fun publish(event: DomainEvent)
    fun publish(events: List<DomainEvent>)
}