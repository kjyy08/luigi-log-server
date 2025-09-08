package cloud.luigi99.blog.common.messaging

import cloud.luigi99.blog.common.domain.DomainEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class DomainEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) : EventPublisher {

    override fun publish(event: DomainEvent) {
        applicationEventPublisher.publishEvent(event)
    }

    override fun publish(events: List<DomainEvent>) {
        events.forEach { event ->
            applicationEventPublisher.publishEvent(event)
        }
    }
}