package cloud.luigi99.blog.common.persistence

import cloud.luigi99.blog.common.fixtures.TestJpaAggregate
import cloud.luigi99.blog.common.fixtures.TestAggregateId
import cloud.luigi99.blog.common.fixtures.TestDomainEvent
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import java.time.LocalDateTime
import java.util.*

/**
 * JpaAggregate의 행위를 검증하는 테스트
 *
 * JPA 환경에서 애그리게이트 루트의 기능을 테스트합니다:
 * - Persistable 인터페이스 구현
 * - 도메인 이벤트 관리 기능
 * - JPA Auditing 연동
 * - 제네릭 타입 안전성
 */
class JpaAggregateTest : BehaviorSpec({

    given("새로 생성된 JpaAggregate가 있을 때") {
        val aggregate = TestJpaAggregate()

        `when`("Persistable 인터페이스 구현을 확인하면") {
            then("getId()는 entityId를 반환한다") {
                aggregate.getId() shouldBe aggregate.entityId
                aggregate.getId().shouldNotBeNull()
            }

            then("isNew()는 새 엔티티이므로 true를 반환한다") {
                aggregate.isNew() shouldBe true
            }
        }

        `when`("도메인 이벤트 관련 기능을 확인하면") {
            then("도메인 이벤트가 없으므로 관련 메서드들이 올바른 값을 반환한다") {
                aggregate.hasDomainEvents() shouldBe false
                aggregate.domainEvents.shouldBeEmpty()
                aggregate.domainEventCount() shouldBe 0
            }
        }

        `when`("JPA Auditing 기능을 확인하면") {
            then("createdAt이 자동으로 설정된다") {
                aggregate.createdAt.shouldNotBeNull()
            }

            then("updatedAt은 초기에 null이다") {
                aggregate.updatedAt shouldBe null
            }

            then("version이 0으로 초기화된다") {
                aggregate.version shouldBe 0L
            }

            then("deleted가 false로 초기화된다") {
                aggregate.deleted shouldBe false
                aggregate.isDeleted() shouldBe false
            }
        }
    }

    given("JpaAggregate에 도메인 이벤트를 추가할 때") {
        val aggregate = TestJpaAggregate()
        val aggregateId = aggregate.entityId.value
        val testEvent = TestDomainEvent(aggregateId, "TestEvent")

        `when`("도메인 이벤트를 추가하면") {
            aggregate.addEvent(testEvent)

            then("도메인 이벤트가 올바르게 관리된다") {
                aggregate.hasDomainEvents() shouldBe true
                aggregate.domainEvents shouldContain testEvent
                aggregate.domainEvents shouldHaveSize 1
                aggregate.domainEventCount() shouldBe 1
            }
        }

        `when`("여러 도메인 이벤트를 추가하면") {
            val event1 = TestDomainEvent(aggregateId, "Event1")
            val event2 = TestDomainEvent(aggregateId, "Event2")

            aggregate.clearDomainEvents()
            aggregate.addEvents(event1, event2)

            then("모든 이벤트가 추가된다") {
                aggregate.domainEventCount() shouldBe 2
                aggregate.domainEvents shouldContain event1
                aggregate.domainEvents shouldContain event2
            }
        }

        `when`("도메인 이벤트를 제거하면") {
            aggregate.clearDomainEvents()

            then("모든 이벤트가 제거된다") {
                aggregate.hasDomainEvents() shouldBe false
                aggregate.domainEvents.shouldBeEmpty()
                aggregate.domainEventCount() shouldBe 0
            }
        }
    }

    given("특정 타입의 도메인 이벤트 관리 시") {
        val aggregate = TestJpaAggregate()
        val aggregateId = aggregate.entityId.value
        val eventA1 = TestDomainEvent(aggregateId, "TypeA")
        val eventA2 = TestDomainEvent(aggregateId, "TypeA")
        val eventB = TestDomainEvent(aggregateId, "TypeB")

        aggregate.addEvents(eventA1, eventA2, eventB)

        `when`("특정 타입의 이벤트 존재 여부를 확인하면") {
            then("존재하는 타입은 true, 없는 타입은 false를 반환한다") {
                aggregate.hasDomainEvent("TypeA") shouldBe true
                aggregate.hasDomainEvent("TypeB") shouldBe true
                aggregate.hasDomainEvent("TypeC") shouldBe false
            }
        }

        `when`("특정 타입의 이벤트만 제거하면") {
            val removedCount = aggregate.clearDomainEvents("TypeA")

            then("해당 타입의 이벤트만 제거되고 개수를 반환한다") {
                removedCount shouldBe 2
                aggregate.hasDomainEvent("TypeA") shouldBe false
                aggregate.hasDomainEvent("TypeB") shouldBe true
                aggregate.domainEventCount() shouldBe 1
            }
        }
    }

    given("수정된 JpaAggregate가 있을 때") {
        val aggregate = TestJpaAggregate()

        // 시간 차이를 만들기 위해 updatedAt 설정
        Thread.sleep(10)
        aggregate.simulateUpdate()

        `when`("isNew() 메서드를 호출하면") {
            then("updatedAt이 설정되었으므로 false를 반환한다") {
                aggregate.isNew() shouldBe false
            }
        }
    }

    given("Soft Delete 기능을 테스트할 때") {
        val aggregate = TestJpaAggregate()

        `when`("onSoftDelete()가 호출되면") {
            aggregate.onSoftDelete()

            then("삭제 상태가 올바르게 설정된다") {
                aggregate.deleted shouldBe true
                aggregate.deletedAt.shouldNotBeNull()
                aggregate.isDeleted() shouldBe true
            }
        }
    }

    given("제네릭 타입 안전성을 확인할 때") {
        val aggregate1 = TestJpaAggregate()
        val aggregate2 = TestJpaAggregate()

        `when`("서로 다른 인스턴스를 비교하면") {
            then("entityId가 다르므로 다른 엔티티로 인식된다") {
                aggregate1.entityId shouldNotBe aggregate2.entityId
                aggregate1 shouldNotBe aggregate2
            }
        }

        `when`("같은 entityId를 가진 인스턴스를 생성하면") {
            val sameEntityId = TestAggregateId()
            val aggregate3 = TestJpaAggregate(sameEntityId)
            val aggregate4 = TestJpaAggregate(sameEntityId)

            then("같은 엔티티로 인식된다") {
                aggregate3.entityId shouldBe aggregate4.entityId
                aggregate3 shouldBe aggregate4
                aggregate3.hashCode() shouldBe aggregate4.hashCode()
            }
        }
    }
})

