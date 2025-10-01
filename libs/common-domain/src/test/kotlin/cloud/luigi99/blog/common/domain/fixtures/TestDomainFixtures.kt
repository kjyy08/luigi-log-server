package cloud.luigi99.blog.common.domain.fixtures

import cloud.luigi99.blog.common.domain.AggregateRoot
import cloud.luigi99.blog.common.domain.BaseEntity
import cloud.luigi99.blog.common.domain.DomainEvent
import cloud.luigi99.blog.common.domain.ValueObject
import java.time.LocalDateTime
import java.util.*

/**
 * 테스트용 BaseEntity 구현체
 *
 * BaseEntity 관련 테스트에서 사용되는 구체 클래스입니다.
 */
class TestEntity(
    override val entityId: TestEntityId,
    override val createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime?
) : BaseEntity<TestEntityId>() {

    constructor(id: java.util.UUID, createdAt: LocalDateTime, updatedAt: LocalDateTime?) :
        this(TestEntityId(id), createdAt, updatedAt)
}

/**
 * 테스트용 AggregateRoot 구현체
 *
 * AggregateRoot 관련 테스트에서 사용되는 구체 클래스입니다.
 */
class TestAggregateRoot(
    override val entityId: TestAggregateId,
    override val createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime?
) : AggregateRoot<TestAggregateId>() {

    constructor(id: java.util.UUID, createdAt: LocalDateTime, updatedAt: LocalDateTime?) :
        this(TestAggregateId(id), createdAt, updatedAt)

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
     * 여러 도메인 이벤트 추가 (테스트용 public 메서드)
     */
    fun addEvents(events: Collection<DomainEvent>) {
        addDomainEvents(events)
    }
}

/**
 * 테스트용 엔티티 식별자
 *
 * BaseEntity 관련 테스트에서 사용되는 범용 식별자입니다.
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
}

/**
 * 테스트용 애그리게이트 식별자
 *
 * AggregateRoot 관련 테스트에서 사용되는 식별자입니다.
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
}

/**
 * 테스트용 도메인 이벤트
 *
 * DomainEvent 관련 테스트에서 사용되는 이벤트입니다.
 */
class TestDomainEvent(
    override val aggregateId: UUID,
    override val eventType: String = "TestEvent"
) : DomainEvent {
    override val eventId: UUID = UUID.randomUUID()
    override val occurredAt: LocalDateTime = LocalDateTime.now()
}