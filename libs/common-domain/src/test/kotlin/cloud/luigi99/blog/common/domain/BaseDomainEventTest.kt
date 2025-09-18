package cloud.luigi99.blog.common.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import java.time.LocalDateTime
import java.util.*

/**
 * BaseDomainEvent의 행위를 검증하는 테스트
 */
class BaseDomainEventTest : BehaviorSpec({

    given("BaseDomainEvent를 상속한 구체 이벤트가 생성될 때") {
        val aggregateId = UUID.randomUUID()
        val eventType = "TestEvent"

        val event = SampleDomainEvent(aggregateId, eventType)

        `when`("이벤트 기본 프로퍼티를 확인하면") {
            then("aggregateId가 올바르게 설정된다") {
                event.aggregateId shouldBe aggregateId
            }

            then("eventType이 올바르게 설정된다") {
                event.eventType shouldBe eventType
            }

            then("eventId가 자동 생성된다") {
                event.eventId shouldNotBe null
                event.eventId.shouldBeInstanceOf<UUID>()
            }

            then("occurredAt이 자동 생성된다") {
                event.occurredAt shouldNotBe null
                event.occurredAt.shouldBeInstanceOf<LocalDateTime>()
            }

            then("eventVersion이 기본값 1로 설정된다") {
                event.eventVersion shouldBe 1
            }

            then("aggregateType이 클래스명에서 자동 추출된다") {
                event.aggregateType shouldBe "SampleDomain"
            }
        }
    }

    given("동일한 eventId를 가진 두 개의 도메인 이벤트가 있을 때") {
        val aggregateId1 = UUID.randomUUID()
        val aggregateId2 = UUID.randomUUID()
        val eventId = UUID.randomUUID()

        val event1 = SampleEventWithCustomId(aggregateId1, "TestEvent1", eventId)
        val event2 = SampleEventWithCustomId(aggregateId2, "TestEvent2", eventId)

        `when`("equals 메서드를 호출하면") {
            val result = event1.equals(event2)

            then("eventId가 같으므로 true를 반환한다") {
                result shouldBe true
            }
        }

        `when`("hashCode 메서드를 호출하면") {
            val hashCode1 = event1.hashCode()
            val hashCode2 = event2.hashCode()

            then("eventId가 같으므로 같은 해시코드를 반환한다") {
                hashCode1 shouldBe hashCode2
            }
        }
    }

    given("서로 다른 eventId를 가진 두 개의 도메인 이벤트가 있을 때") {
        val aggregateId = UUID.randomUUID()
        val event1 = SampleDomainEvent(aggregateId, "TestEvent")
        val event2 = SampleDomainEvent(aggregateId, "TestEvent")

        `when`("equals 메서드를 호출하면") {
            val result = event1.equals(event2)

            then("eventId가 다르므로 false를 반환한다") {
                result shouldBe false
            }
        }

        `when`("hashCode 메서드를 호출하면") {
            val hashCode1 = event1.hashCode()
            val hashCode2 = event2.hashCode()

            then("eventId가 다르므로 다른 해시코드를 반환한다") {
                hashCode1 shouldNotBe hashCode2
            }
        }
    }

    given("BaseDomainEvent가 자기 자신과 비교될 때") {
        val event = SampleDomainEvent(UUID.randomUUID(), "TestEvent")

        `when`("equals 메서드로 자기 자신과 비교하면") {
            val result = event.equals(event)

            then("true를 반환한다") {
                result shouldBe true
            }
        }
    }

    given("BaseDomainEvent가 null과 비교될 때") {
        val event = SampleDomainEvent(UUID.randomUUID(), "TestEvent")

        `when`("equals 메서드로 null과 비교하면") {
            val result = event.equals(null)

            then("false를 반환한다") {
                result shouldBe false
            }
        }
    }

    given("BaseDomainEvent가 다른 타입의 객체와 비교될 때") {
        val event = SampleDomainEvent(UUID.randomUUID(), "TestEvent")
        val otherObject = "문자열 객체"

        `when`("equals 메서드로 다른 타입 객체와 비교하면") {
            val result = event.equals(otherObject)

            then("false를 반환한다") {
                result shouldBe false
            }
        }
    }

    given("BaseDomainEvent의 toString이 호출될 때") {
        val aggregateId = UUID.randomUUID()
        val eventType = "TestEvent"
        val event = SampleDomainEvent(aggregateId, eventType)

        `when`("toString 메서드를 호출하면") {
            val result = event.toString()

            then("모든 주요 정보를 포함한 문자열을 반환한다") {
                result shouldContain "SampleDomainEvent"
                result shouldContain "eventId=${event.eventId}"
                result shouldContain "aggregateId=$aggregateId"
                result shouldContain "eventType='$eventType'"
                result shouldContain "aggregateType='SampleDomain'"
                result shouldContain "occurredAt=${event.occurredAt}"
                result shouldContain "eventVersion=${event.eventVersion}"
            }
        }
    }

    given("BaseDomainEvent의 companion object 메서드들이 있을 때") {
        `when`("generateEventType을 호출하면") {
            val eventType = BaseDomainEvent.generateEventType(SampleDomainEvent::class.java)

            then("클래스 이름이 반환된다") {
                eventType shouldBe "SampleDomainEvent"
            }
        }

        `when`("builder를 호출하면") {
            val aggregateId = UUID.randomUUID()
            val eventType = "TestEvent"
            val builder = BaseDomainEvent.builder(aggregateId, eventType)

            then("EventBuilder 인스턴스가 반환된다") {
                builder.shouldBeInstanceOf<BaseDomainEvent.EventBuilder>()
            }
        }
    }

    given("EventBuilder가 생성되었을 때") {
        val aggregateId = UUID.randomUUID()
        val eventType = "TestEvent"
        val builder = BaseDomainEvent.builder(aggregateId, eventType)

        `when`("기본 설정으로 buildSimple을 호출하면") {
            val event = builder.buildSimple()

            then("기본값으로 이벤트가 생성된다") {
                event.aggregateId shouldBe aggregateId
                event.eventType shouldBe eventType
                event.eventVersion shouldBe 1
                event.eventId shouldNotBe null
                event.occurredAt shouldNotBe null
            }
        }

        `when`("커스텀 eventId를 설정하고 buildSimple을 호출하면") {
            val customEventId = UUID.randomUUID()
            val event = builder.withEventId(customEventId).buildSimple()

            then("설정한 eventId로 이벤트가 생성된다") {
                event.eventId shouldBe customEventId
                event.aggregateId shouldBe aggregateId
                event.eventType shouldBe eventType
            }
        }

        `when`("커스텀 occurredAt을 설정하고 buildSimple을 호출하면") {
            val customOccurredAt = LocalDateTime.of(2024, 1, 1, 12, 0, 0)
            val event = builder.withOccurredAt(customOccurredAt).buildSimple()

            then("설정한 occurredAt으로 이벤트가 생성된다") {
                event.occurredAt shouldBe customOccurredAt
                event.aggregateId shouldBe aggregateId
                event.eventType shouldBe eventType
            }
        }

        `when`("커스텀 eventVersion을 설정하고 buildSimple을 호출하면") {
            val customVersion = 5
            val event = builder.withEventVersion(customVersion).buildSimple()

            then("설정한 eventVersion으로 이벤트가 생성된다") {
                event.eventVersion shouldBe customVersion
                event.aggregateId shouldBe aggregateId
                event.eventType shouldBe eventType
            }
        }

        `when`("모든 커스텀 값을 설정하고 buildSimple을 호출하면") {
            val customEventId = UUID.randomUUID()
            val customOccurredAt = LocalDateTime.of(2024, 1, 1, 12, 0, 0)
            val customVersion = 3

            val event = builder
                .withEventId(customEventId)
                .withOccurredAt(customOccurredAt)
                .withEventVersion(customVersion)
                .buildSimple()

            then("모든 커스텀 값으로 이벤트가 생성된다") {
                event.eventId shouldBe customEventId
                event.occurredAt shouldBe customOccurredAt
                event.eventVersion shouldBe customVersion
                event.aggregateId shouldBe aggregateId
                event.eventType shouldBe eventType
            }
        }
    }

    given("여러 EventBuilder 체이닝 테스트") {
        val aggregateId = UUID.randomUUID()
        val eventType = "ChainedEvent"

        `when`("빌더 메서드들을 체이닝으로 호출하면") {
            val customEventId = UUID.randomUUID()
            val customOccurredAt = LocalDateTime.of(2024, 6, 15, 10, 30, 0)
            val customVersion = 2

            val event = BaseDomainEvent.builder(aggregateId, eventType)
                .withEventId(customEventId)
                .withOccurredAt(customOccurredAt)
                .withEventVersion(customVersion)
                .buildSimple()

            then("체이닝된 모든 설정이 적용된다") {
                event.aggregateId shouldBe aggregateId
                event.eventType shouldBe eventType
                event.eventId shouldBe customEventId
                event.occurredAt shouldBe customOccurredAt
                event.eventVersion shouldBe customVersion
            }
        }
    }

    given("aggregateType 자동 추출 기능 테스트") {
        `when`("Event 접미사가 있는 클래스명일 때") {
            val event = UserRegisteredEvent(UUID.randomUUID(), "UserRegistered")

            then("Event 접미사가 제거된 aggregateType을 반환한다") {
                event.aggregateType shouldBe "UserRegistered"
            }
        }

        `when`("Event 접미사가 없는 클래스명일 때") {
            val event = SampleDomainEvent(UUID.randomUUID(), "TestEvent")

            then("클래스명에서 Event를 제거한 aggregateType을 반환한다") {
                event.aggregateType shouldBe "SampleDomain"
            }
        }
    }
})

/**
 * 테스트용 BaseDomainEvent 구현체
 */
private class SampleDomainEvent(
    aggregateId: UUID,
    eventType: String
) : BaseDomainEvent(aggregateId, eventType)

/**
 * 커스텀 eventId를 가진 테스트용 BaseDomainEvent 구현체
 */
private class SampleEventWithCustomId(
    aggregateId: UUID,
    eventType: String,
    customEventId: UUID
) : BaseDomainEvent(aggregateId, eventType) {
    override val eventId: UUID = customEventId
}

/**
 * Event 접미사를 가진 테스트용 BaseDomainEvent 구현체
 */
private class UserRegisteredEvent(
    aggregateId: UUID,
    eventType: String
) : BaseDomainEvent(aggregateId, eventType)