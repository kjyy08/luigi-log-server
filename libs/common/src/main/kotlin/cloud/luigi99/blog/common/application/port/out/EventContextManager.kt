package cloud.luigi99.blog.common.application.port.out

import cloud.luigi99.blog.common.domain.event.DomainEvent

/**
 * 도메인 이벤트 컨텍스트 관리 인터페이스
 */
interface EventContextManager {
    /**
     * 도메인 이벤트를 추가합니다.
     */
    fun add(event: DomainEvent)

    /**
     * 등록된 모든 이벤트를 초기화합니다.
     */
    fun clear()

    /**
     * 등록된 모든 이벤트를 반환하고 초기화합니다.
     */
    fun getDomainEventsAndClear(): List<DomainEvent>
}
