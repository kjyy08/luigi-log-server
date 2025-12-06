package cloud.luigi99.blog.common.domain.event

import java.time.Instant
import java.util.UUID

/**
 * DDD Domain Event 인터페이스
 * 도메인에서 발생한 중요한 사건을 나타냅니다.
 */
interface DomainEvent {
    /**
     * 이벤트 발생 시각
     */
    val occurredOn: Instant
        get() = Instant.now()

    /**
     * 이벤트 고유 식별자
     */
    val eventId: String
        get() = UUID.randomUUID().toString()
}
