package cloud.luigi99.blog.common.messaging

import cloud.luigi99.blog.common.domain.DomainEvent
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.assertions.throwables.shouldThrow
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDateTime
import java.util.*

class DomainEventPublisherTest : BehaviorSpec({

    given("단일 도메인 이벤트") {
        `when`("이벤트를 발행할 때") {
            then("이벤트가 발행되어야 한다") {
                val applicationEventPublisher = TestApplicationEventPublisher()
                val domainEventPublisher = DomainEventPublisher(applicationEventPublisher)
                val event = TestDomainEvent("test-data")

                domainEventPublisher.publish(event)

                applicationEventPublisher.publishedEvents shouldHaveSize 1
                applicationEventPublisher.publishedEvents[0] shouldBe event
            }
        }
    }

    given("여러 도메인 이벤트") {
        `when`("이벤트 리스트를 발행할 때") {
            then("모든 이벤트가 순서대로 발행되어야 한다") {
                val applicationEventPublisher = TestApplicationEventPublisher()
                val domainEventPublisher = DomainEventPublisher(applicationEventPublisher)
                val event1 = TestDomainEvent("test-data-1")
                val event2 = TestDomainEvent("test-data-2")
                val event3 = TestDomainEvent("test-data-3")
                val events = listOf(event1, event2, event3)

                domainEventPublisher.publish(events)

                applicationEventPublisher.publishedEvents shouldHaveSize 3
                applicationEventPublisher.publishedEvents[0] shouldBe event1
                applicationEventPublisher.publishedEvents[1] shouldBe event2
                applicationEventPublisher.publishedEvents[2] shouldBe event3
            }
        }
    }

    given("빈 이벤트 리스트") {
        `when`("빈 리스트를 발행할 때") {
            then("예외가 발생하지 않고 아무 이벤트도 발행되지 않아야 한다") {
                val applicationEventPublisher = TestApplicationEventPublisher()
                val domainEventPublisher = DomainEventPublisher(applicationEventPublisher)
                val emptyEvents = emptyList<DomainEvent>()

                domainEventPublisher.publish(emptyEvents)

                applicationEventPublisher.publishedEvents shouldHaveSize 0
            }
        }
    }

    given("ApplicationEventPublisher에서 예외가 발생하는 상황") {
        `when`("예외가 발생하도록 설정하고 이벤트를 발행할 때") {
            then("예외가 전파되어야 한다") {
                val applicationEventPublisher = TestApplicationEventPublisher()
                val domainEventPublisher = DomainEventPublisher(applicationEventPublisher)
                val event = TestDomainEvent("test-data")
                val expectedException = RuntimeException("Event publishing failed")

                applicationEventPublisher.shouldThrowException = true
                applicationEventPublisher.exceptionToThrow = expectedException

                val thrownException = shouldThrow<RuntimeException> {
                    domainEventPublisher.publish(event)
                }

                thrownException shouldBe expectedException
                applicationEventPublisher.publishedEvents shouldHaveSize 1
            }
        }
    }

    given("이벤트 발행 순서 확인") {
        `when`("여러 이벤트를 발행할 때") {
            then("이벤트가 입력 순서대로 발행되어야 한다") {
                val applicationEventPublisher = TestApplicationEventPublisher()
                val domainEventPublisher = DomainEventPublisher(applicationEventPublisher)
                val event1 = TestDomainEvent("first")
                val event2 = TestDomainEvent("second")
                val event3 = TestDomainEvent("third")
                val events = listOf(event1, event2, event3)

                domainEventPublisher.publish(events)

                applicationEventPublisher.publishedEvents shouldHaveSize 3
                (applicationEventPublisher.publishedEvents[0] as TestDomainEvent).data shouldBe "first"
                (applicationEventPublisher.publishedEvents[1] as TestDomainEvent).data shouldBe "second"
                (applicationEventPublisher.publishedEvents[2] as TestDomainEvent).data shouldBe "third"
            }
        }
    }
}) {
    /**
     * 테스트용 ApplicationEventPublisher 구현체
     */
    class TestApplicationEventPublisher : ApplicationEventPublisher {
        val publishedEvents = mutableListOf<Any>()
        var shouldThrowException = false
        var exceptionToThrow: RuntimeException? = null

        override fun publishEvent(event: Any) {
            publishedEvents.add(event)
            if (shouldThrowException && exceptionToThrow != null) {
                throw exceptionToThrow!!
            }
        }
    }

    /**
     * 테스트용 도메인 이벤트 클래스
     */
    data class TestDomainEvent(
        val data: String,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: LocalDateTime = LocalDateTime.now(),
        override val aggregateId: UUID = UUID.randomUUID(),
        override val eventType: String = "TestDomainEvent"
    ) : DomainEvent
}