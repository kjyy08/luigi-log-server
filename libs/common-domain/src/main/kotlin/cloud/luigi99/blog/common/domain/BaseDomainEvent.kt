package cloud.luigi99.blog.common.domain

import java.time.LocalDateTime
import java.util.*

/**
 * 도메인 이벤트의 기본 추상 구현체
 *
 * 개인 기술 블로그 프로젝트에서 발생하는 모든 도메인 이벤트의 공통 기능을 제공하는 추상 클래스입니다.
 * DomainEvent 인터페이스의 모든 필수 프로퍼티에 대한 기본 구현을 포함하며,
 * 구체적인 도메인 이벤트 클래스들이 상속받아 사용할 수 있는 공통 기반을 제공합니다.
 *
 * 주요 특징:
 * - 불변성: 생성 후 변경할 수 없는 이벤트 속성들
 * - 자동 생성: eventId와 occurredAt은 생성 시점에 자동 설정
 * - 타입 안전성: Kotlin의 타입 시스템을 활용한 컴파일 타임 검증
 * - 추적 가능성: equals/hashCode를 통한 이벤트 식별 및 추적
 *
 * @property aggregateId 이벤트를 발생시킨 애그리게이트의 고유 식별자
 * @property eventType 이벤트의 종류를 나타내는 문자열
 * @property eventId 이벤트의 고유 식별자 (자동 생성)
 * @property occurredAt 이벤트 발생 시각 (자동 생성)
 */
abstract class BaseDomainEvent(
    override val aggregateId: UUID,
    override val eventType: String
) : DomainEvent {

    /**
     * 이벤트 고유 식별자
     * 생성 시점에 자동으로 UUID가 생성됩니다.
     */
    override val eventId: UUID = UUID.randomUUID()

    /**
     * 이벤트 발생 시각
     * 생성 시점의 현재 시각으로 자동 설정됩니다.
     */
    override val occurredAt: LocalDateTime = LocalDateTime.now()

    /**
     * 이벤트 버전
     * 기본값은 1이며, 하위 클래스에서 필요시 오버라이드 가능합니다.
     */
    override val eventVersion: Int = 1

    /**
     * 애그리게이트 타입
     * 클래스명에서 "Event" 접미사를 제거하여 자동 추출합니다.
     * 예: UserRegisteredEvent → "UserRegistered"
     */
    override val aggregateType: String
        get() = this::class.simpleName?.replace("Event", "") ?: "Unknown"

    /**
     * 객체 동등성 비교
     * eventId를 기준으로 두 이벤트가 같은지 판단합니다.
     *
     * @param other 비교할 객체
     * @return eventId가 같으면 true, 다르면 false
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseDomainEvent) return false
        return eventId == other.eventId
    }

    /**
     * 해시코드 생성
     * eventId를 기준으로 해시코드를 생성합니다.
     *
     * @return eventId 기반의 해시코드
     */
    override fun hashCode(): Int {
        return eventId.hashCode()
    }

    /**
     * 문자열 표현
     * 디버깅과 로깅을 위한 이벤트의 문자열 표현을 제공합니다.
     *
     * @return 이벤트의 주요 정보를 포함한 문자열
     */
    override fun toString(): String {
        return "${this::class.simpleName}(" +
                "eventId=$eventId, " +
                "aggregateId=$aggregateId, " +
                "eventType='$eventType', " +
                "aggregateType='$aggregateType', " +
                "occurredAt=$occurredAt, " +
                "eventVersion=$eventVersion" +
                ")"
    }

    companion object {
        /**
         * 이벤트 타입 생성 헬퍼 메서드
         * 클래스명에서 이벤트 타입을 자동 생성합니다.
         *
         * @param eventClass 이벤트 클래스
         * @return 이벤트 타입 문자열
         */
        fun generateEventType(eventClass: Class<*>): String {
            return eventClass.simpleName
        }

        /**
         * 이벤트 빌더 생성 헬퍼 메서드
         * 공통 이벤트 생성 로직을 위한 빌더 패턴 지원
         *
         * @param aggregateId 애그리게이트 ID
         * @param eventType 이벤트 타입
         * @return 이벤트 빌더 인스턴스
         */
        fun builder(aggregateId: UUID, eventType: String): EventBuilder {
            return EventBuilder(aggregateId, eventType)
        }
    }

    /**
     * 이벤트 빌더 클래스
     * 복잡한 이벤트 생성을 위한 빌더 패턴을 제공합니다.
     */
    class EventBuilder internal constructor(
        private val aggregateId: UUID,
        private val eventType: String
    ) {
        private var customEventId: UUID? = null
        private var customOccurredAt: LocalDateTime? = null
        private var customEventVersion: Int = 1

        /**
         * 커스텀 이벤트 ID 설정
         * 기본 자동 생성 대신 특정 UUID를 사용하고 싶을 때 사용합니다.
         */
        fun withEventId(eventId: UUID): EventBuilder {
            this.customEventId = eventId
            return this
        }

        /**
         * 커스텀 발생 시각 설정
         * 현재 시각 대신 특정 시각을 설정하고 싶을 때 사용합니다.
         */
        fun withOccurredAt(occurredAt: LocalDateTime): EventBuilder {
            this.customOccurredAt = occurredAt
            return this
        }

        /**
         * 이벤트 버전 설정
         * 기본값 1 대신 다른 버전을 사용하고 싶을 때 사용합니다.
         */
        fun withEventVersion(version: Int): EventBuilder {
            this.customEventVersion = version
            return this
        }

        /**
         * 설정된 값들로 간단한 도메인 이벤트를 빌드합니다.
         * 테스트나 간단한 용도로 사용할 수 있는 기본 구현체를 반환합니다.
         */
        fun buildSimple(): BaseDomainEvent {
            return object : BaseDomainEvent(aggregateId, eventType) {
                override val eventId: UUID = customEventId ?: super.eventId
                override val occurredAt: LocalDateTime = customOccurredAt ?: super.occurredAt
                override val eventVersion: Int = customEventVersion
            }
        }
    }
}