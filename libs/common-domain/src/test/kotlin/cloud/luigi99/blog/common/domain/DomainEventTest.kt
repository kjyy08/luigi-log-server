package cloud.luigi99.blog.common.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.time.LocalDateTime
import java.util.*

/**
 * DomainEvent의 행위를 검증하는 테스트
 */
class DomainEventTest : BehaviorSpec({

    given("DomainEvent 구현체가 있을 때") {
        val eventId = UUID.randomUUID()
        val aggregateId = UUID.randomUUID()
        val occurredAt = LocalDateTime.now()
        val eventType = "PostCreated"

        val domainEvent = DomainEventTestImpl(eventId, aggregateId, occurredAt, eventType)

        `when`("기본 속성들을 확인하면") {
            then("모든 속성이 올바르게 설정된다") {
                domainEvent.eventId shouldBe eventId
                domainEvent.aggregateId shouldBe aggregateId
                domainEvent.occurredAt shouldBe occurredAt
                domainEvent.eventType shouldBe eventType
            }
        }

        `when`("기본 이벤트 버전을 확인하면") {
            val eventVersion = domainEvent.eventVersion

            then("기본값 1을 반환한다") {
                eventVersion shouldBe 1
            }
        }

        `when`("애그리게이트 타입을 확인하면") {
            val aggregateType = domainEvent.aggregateType

            then("클래스명에서 Event를 제거한 타입을 반환한다") {
                aggregateType shouldBe "TestDomain"
            }
        }
    }

    given("이벤트 타입 확인 기능을 테스트할 때") {
        val domainEvent = DomainEventTestImpl(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), "UserRegistered")

        `when`("일치하는 이벤트 타입으로 확인하면") {
            val isCorrectType = domainEvent.isEventType("UserRegistered")

            then("true를 반환한다") {
                isCorrectType shouldBe true
            }
        }

        `when`("일치하지 않는 이벤트 타입으로 확인하면") {
            val isCorrectType = domainEvent.isEventType("UserDeleted")

            then("false를 반환한다") {
                isCorrectType shouldBe false
            }
        }
    }

    given("애그리게이트 확인 기능을 테스트할 때") {
        val aggregateId = UUID.randomUUID()
        val domainEvent = DomainEventTestImpl(UUID.randomUUID(), aggregateId, LocalDateTime.now(), "PostCreated")

        `when`("일치하는 애그리게이트 ID로 확인하면") {
            val isFromAggregate = domainEvent.isFromAggregate(aggregateId)

            then("true를 반환한다") {
                isFromAggregate shouldBe true
            }
        }

        `when`("일치하지 않는 애그리게이트 ID로 확인하면") {
            val differentId = UUID.randomUUID()
            val isFromAggregate = domainEvent.isFromAggregate(differentId)

            then("false를 반환한다") {
                isFromAggregate shouldBe false
            }
        }
    }

    given("시간 비교 기능을 테스트할 때") {
        val baseTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0)
        val domainEvent = DomainEventTestImpl(UUID.randomUUID(), UUID.randomUUID(), baseTime, "TestEvent")

        `when`("이벤트 발생 시각 이전 시간과 비교하면") {
            val earlierTime = baseTime.minusHours(1)
            val occurredAfter = domainEvent.occurredAfter(earlierTime)

            then("true를 반환한다") {
                occurredAfter shouldBe true
            }
        }

        `when`("이벤트 발생 시각 이후 시간과 비교하면") {
            val laterTime = baseTime.plusHours(1)
            val occurredAfter = domainEvent.occurredAfter(laterTime)

            then("false를 반환한다") {
                occurredAfter shouldBe false
            }
        }

        `when`("이벤트 발생 시각 이전 시간으로 before 확인하면") {
            val laterTime = baseTime.plusHours(1)
            val occurredBefore = domainEvent.occurredBefore(laterTime)

            then("true를 반환한다") {
                occurredBefore shouldBe true
            }
        }

        `when`("이벤트 발생 시각 이후 시간으로 before 확인하면") {
            val earlierTime = baseTime.minusHours(1)
            val occurredBefore = domainEvent.occurredBefore(earlierTime)

            then("false를 반환한다") {
                occurredBefore shouldBe false
            }
        }
    }

    given("커스텀 이벤트 버전을 가진 DomainEvent가 있을 때") {
        val customVersionEvent = CustomVersionEvent(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), "CustomEvent", 5)

        `when`("이벤트 버전을 확인하면") {
            val eventVersion = customVersionEvent.eventVersion

            then("커스텀 버전을 반환한다") {
                eventVersion shouldBe 5
            }
        }
    }

    given("커스텀 애그리게이트 타입을 가진 DomainEvent가 있을 때") {
        val customTypeEvent = CustomAggregateTypeEvent(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), "CustomEvent")

        `when`("애그리게이트 타입을 확인하면") {
            val aggregateType = customTypeEvent.aggregateType

            then("커스텀 타입을 반환한다") {
                aggregateType shouldBe "CustomAggregate"
            }
        }
    }

    given("이벤트명에 Event가 포함되지 않은 클래스가 있을 때") {
        val noEventSuffixEvent = NoEventSuffixTest(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), "TestEvent")

        `when`("애그리게이트 타입을 확인하면") {
            val aggregateType = noEventSuffixEvent.aggregateType

            then("클래스명에서 Event를 제거한 결과를 반환한다") {
                // "NoEventSuffixTest"에서 "Event"를 제거하면 "NoSuffixTest"가 됨
                aggregateType shouldBe "NoSuffixTest"
            }
        }
    }
})

/**
 * 테스트용 DomainEvent 구현체
 */
private class DomainEventTestImpl(
    override val eventId: UUID,
    override val aggregateId: UUID,
    override val occurredAt: LocalDateTime,
    override val eventType: String
) : DomainEvent {
    override val aggregateType: String
        get() = "TestDomain"
}

/**
 * 커스텀 이벤트 버전을 가진 테스트용 DomainEvent 구현체
 */
private class CustomVersionEvent(
    override val eventId: UUID,
    override val aggregateId: UUID,
    override val occurredAt: LocalDateTime,
    override val eventType: String,
    override val eventVersion: Int
) : DomainEvent

/**
 * 커스텀 애그리게이트 타입을 가진 테스트용 DomainEvent 구현체
 */
private class CustomAggregateTypeEvent(
    override val eventId: UUID,
    override val aggregateId: UUID,
    override val occurredAt: LocalDateTime,
    override val eventType: String
) : DomainEvent {
    override val aggregateType: String
        get() = "CustomAggregate"
}

/**
 * Event 접미사가 없는 테스트용 DomainEvent 구현체
 */
private class NoEventSuffixTest(
    override val eventId: UUID,
    override val aggregateId: UUID,
    override val occurredAt: LocalDateTime,
    override val eventType: String
) : DomainEvent