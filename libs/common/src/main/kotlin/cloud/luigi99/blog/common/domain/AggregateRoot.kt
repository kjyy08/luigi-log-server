package cloud.luigi99.blog.common.domain

import cloud.luigi99.blog.common.domain.event.DomainEvent
import cloud.luigi99.blog.common.domain.event.EventManager
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

/**
 * DDD Aggregate Root 추상 클래스
 * 트랜잭션 일관성 경계를 표시하며, 도메인 이벤트를 발행할 수 있습니다.
 */
abstract class AggregateRoot<out T : ValueObject> : DomainEntity<T>() {
    private val eventManager = EventManager

    /**
     * 도메인 이벤트를 등록합니다.
     */
    protected fun registerEvent(event: DomainEvent): DomainEvent {
        log.debug { "register event: $event" }
        eventManager.add(event)
        return event
    }

    /**
     * 등록된 도메인 이벤트를 초기화합니다.
     */
    fun clearEvents() = eventManager.clear()

    /**
     * 등록된 도메인 이벤트를 반환하고 초기화합니다.
     */
    fun getEvents() = eventManager.toListAndClear()
}
