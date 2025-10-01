package cloud.luigi99.blog.common.domain

import cloud.luigi99.blog.common.domain.fixtures.TestAggregateRoot
import cloud.luigi99.blog.common.domain.fixtures.TestAggregateId
import cloud.luigi99.blog.common.domain.fixtures.TestDomainEvent
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import java.time.LocalDateTime
import java.util.*

/**
 * AggregateRoot의 행위를 검증하는 테스트
 */
class AggregateRootTest : BehaviorSpec({

    given("새로 생성된 AggregateRoot가 있을 때") {
        val aggregate = TestAggregateRoot(UUID.randomUUID(), LocalDateTime.now(), null)

        `when`("도메인 이벤트 존재 여부를 확인하면") {
            val hasEvents = aggregate.hasDomainEvents()

            then("도메인 이벤트가 없으므로 false를 반환한다") {
                hasEvents shouldBe false
            }
        }

        `when`("도메인 이벤트 목록을 조회하면") {
            val events = aggregate.domainEvents

            then("빈 목록을 반환한다") {
                events.shouldBeEmpty()
            }
        }

        `when`("도메인 이벤트 개수를 확인하면") {
            val count = aggregate.domainEventCount()

            then("0을 반환한다") {
                count shouldBe 0
            }
        }
    }

    given("AggregateRoot에 도메인 이벤트를 추가할 때") {
        val aggregateId = UUID.randomUUID()
        val aggregate = TestAggregateRoot(aggregateId, LocalDateTime.now(), null)
        val testEvent = TestDomainEvent(aggregateId, "TestEvent")

        `when`("도메인 이벤트를 추가하면") {
            aggregate.addEvent(testEvent)

            then("도메인 이벤트가 존재한다") {
                aggregate.hasDomainEvents() shouldBe true
            }

            then("도메인 이벤트 목록에 추가된 이벤트가 포함된다") {
                aggregate.domainEvents shouldContain testEvent
                aggregate.domainEvents shouldHaveSize 1
            }

            then("도메인 이벤트 개수가 1이다") {
                aggregate.domainEventCount() shouldBe 1
            }
        }
    }

    given("AggregateRoot에 여러 도메인 이벤트를 추가할 때") {
        val aggregateId = UUID.randomUUID()
        val aggregate = TestAggregateRoot(aggregateId, LocalDateTime.now(), null)
        val event1 = TestDomainEvent(aggregateId, "Event1")
        val event2 = TestDomainEvent(aggregateId, "Event2")
        val event3 = TestDomainEvent(aggregateId, "Event3")

        `when`("여러 이벤트를 한 번에 추가하면") {
            aggregate.addEvents(event1, event2, event3)

            then("모든 이벤트가 추가된다") {
                aggregate.domainEvents shouldHaveSize 3
                aggregate.domainEvents shouldContain event1
                aggregate.domainEvents shouldContain event2
                aggregate.domainEvents shouldContain event3
            }
        }

        `when`("컬렉션으로 이벤트를 추가하면") {
            aggregate.clearDomainEvents()
            val eventList = listOf(event1, event2)
            aggregate.addEvents(eventList)

            then("컬렉션의 모든 이벤트가 추가된다") {
                aggregate.domainEvents shouldHaveSize 2
                aggregate.domainEvents shouldContain event1
                aggregate.domainEvents shouldContain event2
            }
        }
    }

    given("특정 타입의 도메인 이벤트를 확인할 때") {
        val aggregateId = UUID.randomUUID()
        val aggregate = TestAggregateRoot(aggregateId, LocalDateTime.now(), null)
        val event1 = TestDomainEvent(aggregateId, "TypeA")
        val event2 = TestDomainEvent(aggregateId, "TypeB")

        aggregate.addEvents(event1, event2)

        `when`("존재하는 이벤트 타입을 확인하면") {
            val hasTypeA = aggregate.hasDomainEvent("TypeA")
            val hasTypeB = aggregate.hasDomainEvent("TypeB")

            then("true를 반환한다") {
                hasTypeA shouldBe true
                hasTypeB shouldBe true
            }
        }

        `when`("존재하지 않는 이벤트 타입을 확인하면") {
            val hasTypeC = aggregate.hasDomainEvent("TypeC")

            then("false를 반환한다") {
                hasTypeC shouldBe false
            }
        }
    }

    given("도메인 이벤트가 있는 AggregateRoot에서 이벤트를 제거할 때") {
        val aggregateId = UUID.randomUUID()
        val aggregate = TestAggregateRoot(aggregateId, LocalDateTime.now(), null)
        val event1 = TestDomainEvent(aggregateId, "TypeA")
        val event2 = TestDomainEvent(aggregateId, "TypeB")
        val event3 = TestDomainEvent(aggregateId, "TypeA")

        aggregate.addEvents(event1, event2, event3)

        `when`("모든 도메인 이벤트를 제거하면") {
            aggregate.clearDomainEvents()

            then("도메인 이벤트가 없어진다") {
                aggregate.hasDomainEvents() shouldBe false
                aggregate.domainEvents.shouldBeEmpty()
                aggregate.domainEventCount() shouldBe 0
            }
        }

        `when`("특정 타입의 도메인 이벤트만 제거하면") {
            aggregate.addEvents(event1, event2, event3)
            val removedCount = aggregate.clearDomainEvents("TypeA")

            then("해당 타입의 이벤트만 제거되고 제거된 개수를 반환한다") {
                removedCount shouldBe 2
                aggregate.domainEventCount() shouldBe 1
                aggregate.hasDomainEvent("TypeA") shouldBe false
                aggregate.hasDomainEvent("TypeB") shouldBe true
            }
        }
    }

    given("도메인 이벤트 목록이 읽기 전용인지 확인할 때") {
        val aggregateId = UUID.randomUUID()
        val aggregate = TestAggregateRoot(aggregateId, LocalDateTime.now(), null)
        val testEvent = TestDomainEvent(aggregateId, "TestEvent")

        aggregate.addEvent(testEvent)

        `when`("도메인 이벤트 목록을 여러 번 조회하면") {
            val events1 = aggregate.domainEvents
            val events2 = aggregate.domainEvents

            then("매번 새로운 리스트 인스턴스를 반환한다") {
                events1 shouldBe events2
                (events1 === events2) shouldBe false
            }
        }
    }
})

