package cloud.luigi99.blog.common.domain.fixtures

import cloud.luigi99.blog.common.domain.AggregateRoot
import cloud.luigi99.blog.common.domain.BaseEntity
import cloud.luigi99.blog.common.domain.DomainEvent
import java.time.LocalDateTime

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