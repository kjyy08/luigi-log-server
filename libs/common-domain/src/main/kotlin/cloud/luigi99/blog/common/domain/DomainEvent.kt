package cloud.luigi99.blog.common.domain

import java.time.LocalDateTime
import java.util.*

/**
 * 도메인 이벤트(Domain Event)의 기본 인터페이스
 *
 * 개인 기술 블로그 프로젝트에서 발생하는 모든 도메인 이벤트는 이 인터페이스를 구현하여
 * 일관된 이벤트 구조와 메타데이터를 제공받습니다.
 *
 * 도메인 이벤트는 다음 특징을 가집니다:
 * - 불변성: 한 번 생성된 이벤트는 변경되지 않음
 * - 과거 시제: 이미 발생한 사건을 나타냄
 * - 비즈니스 의미: 도메인 전문가가 이해할 수 있는 용어 사용
 */
interface DomainEvent {

    /**
     * 이벤트의 고유 식별자
     * 각 이벤트를 유일하게 식별하기 위한 UUID
     */
    val eventId: UUID

    /**
     * 이벤트 발생 시각
     * 이벤트가 실제로 발생한 시점을 기록
     */
    val occurredAt: LocalDateTime

    /**
     * 이벤트를 발생시킨 애그리게이트의 식별자
     * 어떤 애그리게이트에서 이벤트가 발생했는지 추적
     */
    val aggregateId: UUID

    /**
     * 이벤트 타입
     * 이벤트의 종류를 나타내는 문자열 (예: "PostCreated", "UserRegistered")
     */
    val eventType: String

    /**
     * 이벤트 버전
     * 이벤트 스키마의 버전을 나타냄 (기본값: 1)
     */
    val eventVersion: Int
        get() = 1

    /**
     * 애그리게이트 타입
     * 이벤트를 발생시킨 애그리게이트의 타입 (예: "Post", "User")
     */
    val aggregateType: String
        get() = this::class.simpleName?.replace("Event", "") ?: "Unknown"

    /**
     * 이벤트가 특정 타입인지 확인합니다.
     *
     * @param type 확인할 이벤트 타입
     * @return 이벤트 타입이 일치하면 true, 다르면 false
     */
    fun isEventType(type: String): Boolean {
        return eventType == type
    }

    /**
     * 이벤트가 특정 애그리게이트에서 발생했는지 확인합니다.
     *
     * @param aggregateId 확인할 애그리게이트 ID
     * @return 애그리게이트 ID가 일치하면 true, 다르면 false
     */
    fun isFromAggregate(aggregateId: UUID): Boolean {
        return this.aggregateId == aggregateId
    }

    /**
     * 이벤트가 특정 시간 이후에 발생했는지 확인합니다.
     *
     * @param timestamp 기준 시각
     * @return 이벤트가 기준 시각 이후에 발생했으면 true, 그렇지 않으면 false
     */
    fun occurredAfter(timestamp: LocalDateTime): Boolean {
        return occurredAt.isAfter(timestamp)
    }

    /**
     * 이벤트가 특정 시간 이전에 발생했는지 확인합니다.
     *
     * @param timestamp 기준 시각
     * @return 이벤트가 기준 시각 이전에 발생했으면 true, 그렇지 않으면 false
     */
    fun occurredBefore(timestamp: LocalDateTime): Boolean {
        return occurredAt.isBefore(timestamp)
    }
}