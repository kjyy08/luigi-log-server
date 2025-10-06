package cloud.luigi99.blog.common.fixtures

import cloud.luigi99.blog.common.domain.DomainEvent
import cloud.luigi99.blog.common.domain.ValueObject
import cloud.luigi99.blog.common.persistence.JpaAggregate
import java.time.LocalDateTime
import java.util.*

/**
 * 테스트용 엔티티 식별자
 */
data class TestEntityId(
    val value: UUID = UUID.randomUUID()
) : ValueObject() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TestEntityId) return false
        return value == other.value
    }

    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = value.toString()
    override fun isValid(): Boolean = true
}

/**
 * 테스트용 애그리게이트 식별자
 */
data class TestAggregateId(
    val value: UUID = UUID.randomUUID()
) : ValueObject() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TestAggregateId) return false
        return value == other.value
    }

    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = value.toString()
    override fun isValid(): Boolean = true
}

/**
 * 테스트용 도메인 이벤트
 */
class TestDomainEvent(
    override val aggregateId: UUID,
    override val eventType: String = "TestEvent"
) : DomainEvent {
    override val eventId: UUID = UUID.randomUUID()
    override val occurredAt: LocalDateTime = LocalDateTime.now()
}

/**
 * 테스트용 JPA 엔티티 (단순 엔티티, 도메인 이벤트 불필요)
 *
 * 만약 도메인 이벤트가 필요 없는 단순 엔티티가 필요하다면
 * 이 클래스를 사용할 수 있습니다. 하지만 대부분의 경우 TestJpaAggregate를 사용합니다.
 */
class TestJpaEntity(
    override val entityId: TestEntityId = TestEntityId()
) : JpaAggregate<TestEntityId>()

/**
 * 테스트용 JPA 애그리게이트
 *
 * JpaAggregate 관련 테스트에서 사용되는 구체 클래스입니다.
 */
class TestJpaAggregate(
    override val entityId: TestAggregateId = TestAggregateId()
) : JpaAggregate<TestAggregateId>() {

    /**
     * 도메인 이벤트 추가 (테스트용 public 메서드)
     */
    fun addEvent(event: DomainEvent) {
        addDomainEvent(event)
    }

    /**
     * 여러 도메인 이벤트 추가 (테스트용 public 메서드)
     */
    fun addEvents(vararg events: DomainEvent) {
        addDomainEvents(*events)
    }

    /**
     * 엔티티 수정 시뮬레이션 (updatedAt 설정)
     */
    fun simulateUpdate() {
        updatedAt = LocalDateTime.now()
    }
}