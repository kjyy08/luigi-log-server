package cloud.luigi99.blog.adapter.message.spring

import cloud.luigi99.blog.common.application.port.out.EventContextManager
import cloud.luigi99.blog.common.domain.event.EventManager
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.context.annotation.Configuration

private val log = KotlinLogging.logger {}

@Configuration
class EventConfiguration(private val eventContextManager: EventContextManager) {
    @PostConstruct
    fun init() {
        log.info { "Initializing EventManager with SpringEventContextManager" }
        EventManager.eventContextManager = eventContextManager
    }
}
