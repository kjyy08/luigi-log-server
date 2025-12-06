package cloud.luigi99.blog.adapter.message.spring

import cloud.luigi99.blog.common.application.port.out.EventContextManager
import cloud.luigi99.blog.common.domain.event.DomainEvent
import mu.KotlinLogging
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class SpringEventContextManager : EventContextManager {
    private val events = ThreadLocal.withInitial { mutableListOf<DomainEvent>() }

    override fun add(event: DomainEvent) {
        events.get().add(event)
        log.debug { "Added domain event: ${event::class.simpleName}" }
    }

    override fun clear() {
        events.get().clear()
        events.remove()
        log.debug { "Cleared domain events for current thread" }
    }

    override fun getDomainEventsAndClear(): List<DomainEvent> {
        val eventList = events.get().toList()
        clear()
        return eventList
    }
}
