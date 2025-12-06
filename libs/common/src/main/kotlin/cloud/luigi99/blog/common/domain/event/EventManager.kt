package cloud.luigi99.blog.common.domain.event

import cloud.luigi99.blog.common.application.port.out.EventContextManager

/**
 * 도메인 이벤트 관리 파사드
 * 실제 구현체(EventContextManager)를 외부에서 주입받아 사용합니다.
 */
object EventManager {
    lateinit var eventContextManager: EventContextManager

    fun add(event: DomainEvent) {
        eventContextManager.add(event)
    }

    fun clear() {
        eventContextManager.clear()
    }

    fun toListAndClear(): List<DomainEvent> = eventContextManager.getDomainEventsAndClear()
}
