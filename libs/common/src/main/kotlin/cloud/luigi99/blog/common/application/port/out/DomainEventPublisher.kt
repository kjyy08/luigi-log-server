package cloud.luigi99.blog.common.application.port.out

import cloud.luigi99.blog.common.domain.event.DomainEvent

/**
 * 도메인 이벤트 발행 포트
 * 저장된 이벤트를 실제 메시지 브로커나 이벤트 버스로 발행합니다.
 */
interface DomainEventPublisher {
    fun publish(event: DomainEvent)
}
