package cloud.luigi99.blog.adapter.message.spring

import cloud.luigi99.blog.common.application.port.out.DomainEventPublisher
import cloud.luigi99.blog.common.domain.event.DomainEvent
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class SpringDomainEventPublisher(private val eventPublisher: ApplicationEventPublisher) : DomainEventPublisher {
    override fun publish(event: DomainEvent) {
        log.debug { "Publishing event: ${event::class.simpleName}" }
        eventPublisher.publishEvent(event)
    }
}
