package cloud.luigi99.blog.common.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import java.time.LocalDateTime
import java.util.*
import kotlin.test.*

@DisplayName("AggregateRoot 인터페이스 및 AbstractAggregateRoot 테스트")
class AggregateRootTest {

    /**
     * 테스트용 AggregateRoot 구현체
     */
    private data class TestAggregateRoot(
        override val id: UUID = UUID.randomUUID(),
        override val createdAt: LocalDateTime = LocalDateTime.now(),
        override val updatedAt: LocalDateTime? = null,
        private val _domainEvents: MutableList<DomainEvent> = mutableListOf()
    ) : AggregateRoot {

        override val domainEvents: List<DomainEvent>
            get() = _domainEvents.toList()

        override fun addDomainEvent(event: DomainEvent) {
            _domainEvents.add(event)
        }

        override fun clearDomainEvents() {
            _domainEvents.clear()
        }
    }

    /**
     * 테스트용 AbstractAggregateRoot 구현체
     */
    private class TestAbstractAggregateRoot(
        override val id: UUID = UUID.randomUUID(),
        override val createdAt: LocalDateTime = LocalDateTime.now(),
        override val updatedAt: LocalDateTime? = null
    ) : AbstractAggregateRoot() {

        // 테스트를 위해 protected 메서드들을 노출하는 public 메서드들
        fun addMultipleDomainEvents(vararg events: DomainEvent) {
            addDomainEvents(*events)
        }

        fun addDomainEventsFromCollection(events: Collection<DomainEvent>) {
            addDomainEvents(events)
        }

        fun countEventsOfType(eventType: Class<out DomainEvent>): Int {
            return countDomainEventsOfType(eventType)
        }
    }

    private lateinit var testAggregateId: UUID
    private lateinit var testEventContext: EventContext

    @BeforeEach
    fun setUp() {
        testAggregateId = UUID.randomUUID()
        testEventContext = EventContext(
            source = "test-service",
            initiatedBy = "test-user",
            metadata = mapOf("test" to "value")
        )
    }

    @Nested
    @DisplayName("AggregateRoot 인터페이스 기본 계약 테스트")
    inner class AggregateRootContractTest {

        @Test
        @DisplayName("새로운 AggregateRoot는 빈 도메인 이벤트 목록을 가진다")
        fun `new AggregateRoot has empty domain events list`() {
            // Given
            val aggregateRoot = TestAggregateRoot()

            // When & Then
            assertAll(
                { assertTrue(aggregateRoot.domainEvents.isEmpty()) },
                { assertNotNull(aggregateRoot.id) },
                { assertNotNull(aggregateRoot.createdAt) },
                { assertTrue(aggregateRoot.isNew()) }
            )
        }

        @Test
        @DisplayName("도메인 이벤트를 추가할 수 있다")
        fun `can add domain event`() {
            // Given
            val aggregateRoot = TestAggregateRoot()
            val event = EntityCreated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload("User", mapOf("name" to "John"))
            )

            // When
            aggregateRoot.addDomainEvent(event)

            // Then
            assertAll(
                { assertEquals(1, aggregateRoot.domainEvents.size) },
                { assertEquals(event, aggregateRoot.domainEvents.first()) }
            )
        }

        @Test
        @DisplayName("여러 도메인 이벤트를 추가할 수 있다")
        fun `can add multiple domain events`() {
            // Given
            val aggregateRoot = TestAggregateRoot()
            val event1 = EntityCreated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload("User", mapOf())
            )
            val event2 = EntityUpdated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityUpdatedPayload("User", mapOf(), mapOf("status" to "ACTIVE"), setOf("status"))
            )

            // When
            aggregateRoot.addDomainEvent(event1)
            aggregateRoot.addDomainEvent(event2)

            // Then
            assertAll(
                { assertEquals(2, aggregateRoot.domainEvents.size) },
                { assertEquals(event1, aggregateRoot.domainEvents[0]) },
                { assertEquals(event2, aggregateRoot.domainEvents[1]) }
            )
        }

        @Test
        @DisplayName("도메인 이벤트를 모두 제거할 수 있다")
        fun `can clear all domain events`() {
            // Given
            val aggregateRoot = TestAggregateRoot()
            val event = EntityCreated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload("User", mapOf())
            )
            aggregateRoot.addDomainEvent(event)

            // When
            aggregateRoot.clearDomainEvents()

            // Then
            assertTrue(aggregateRoot.domainEvents.isEmpty())
        }

        @Test
        @DisplayName("도메인 이벤트 목록은 불변이어야 한다")
        fun `domain events list should be immutable`() {
            // Given
            val aggregateRoot = TestAggregateRoot()
            val event = EntityCreated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload("User", mapOf())
            )
            aggregateRoot.addDomainEvent(event)

            // When
            val events = aggregateRoot.domainEvents

            // Then
            assertFailsWith<UnsupportedOperationException> {
                (events as MutableList).add(event)
            }
        }
    }

    @Nested
    @DisplayName("AbstractAggregateRoot 구현체 테스트")
    inner class AbstractAggregateRootTest {

        @Test
        @DisplayName("AbstractAggregateRoot 기본 동작 확인")
        fun `AbstractAggregateRoot basic functionality`() {
            // Given
            val aggregateRoot = TestAbstractAggregateRoot()

            // When & Then
            assertAll(
                { assertTrue(aggregateRoot.domainEvents.isEmpty()) },
                { assertFalse(aggregateRoot.hasDomainEvents()) },
                { assertNotNull(aggregateRoot.id) },
                { assertNotNull(aggregateRoot.createdAt) }
            )
        }

        @Test
        @DisplayName("단일 도메인 이벤트 추가")
        fun `add single domain event`() {
            // Given
            val aggregateRoot = TestAbstractAggregateRoot()
            val event = EntityCreated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload("User", mapOf())
            )

            // When
            aggregateRoot.addDomainEvent(event)

            // Then
            assertAll(
                { assertEquals(1, aggregateRoot.domainEvents.size) },
                { assertTrue(aggregateRoot.hasDomainEvents()) },
                { assertEquals(event, aggregateRoot.domainEvents.first()) }
            )
        }

        @Test
        @DisplayName("여러 도메인 이벤트를 vararg로 추가")
        fun `add multiple domain events with vararg`() {
            // Given
            val aggregateRoot = TestAbstractAggregateRoot()
            val event1 = EntityCreated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload("User", mapOf())
            )
            val event2 = EntityUpdated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityUpdatedPayload("User", mapOf(), mapOf(), setOf())
            )
            val event3 = EntityStateChanged(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityStateChangedPayload("User", "INACTIVE", "ACTIVE")
            )

            // When
            aggregateRoot.addMultipleDomainEvents(event1, event2, event3)

            // Then
            assertAll(
                { assertEquals(3, aggregateRoot.domainEvents.size) },
                { assertTrue(aggregateRoot.hasDomainEvents()) },
                { assertEquals(event1, aggregateRoot.domainEvents[0]) },
                { assertEquals(event2, aggregateRoot.domainEvents[1]) },
                { assertEquals(event3, aggregateRoot.domainEvents[2]) }
            )
        }

        @Test
        @DisplayName("여러 도메인 이벤트를 컬렉션으로 추가")
        fun `add multiple domain events with collection`() {
            // Given
            val aggregateRoot = TestAbstractAggregateRoot()
            val events = listOf(
                EntityCreated(
                    aggregateId = testAggregateId,
                    eventContext = testEventContext,
                    payload = EntityCreatedPayload("User", mapOf())
                ),
                EntityUpdated(
                    aggregateId = testAggregateId,
                    eventContext = testEventContext,
                    payload = EntityUpdatedPayload("User", mapOf(), mapOf(), setOf())
                )
            )

            // When
            aggregateRoot.addDomainEventsFromCollection(events)

            // Then
            assertAll(
                { assertEquals(2, aggregateRoot.domainEvents.size) },
                { assertTrue(aggregateRoot.hasDomainEvents()) },
                { assertEquals(events[0], aggregateRoot.domainEvents[0]) },
                { assertEquals(events[1], aggregateRoot.domainEvents[1]) }
            )
        }

        @Test
        @DisplayName("특정 타입의 도메인 이벤트 개수 확인")
        fun `count domain events of specific type`() {
            // Given
            val aggregateRoot = TestAbstractAggregateRoot()
            val createdEvent1 = EntityCreated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload("User", mapOf())
            )
            val createdEvent2 = EntityCreated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload("Order", mapOf())
            )
            val updatedEvent = EntityUpdated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityUpdatedPayload("User", mapOf(), mapOf(), setOf())
            )

            aggregateRoot.addMultipleDomainEvents(createdEvent1, createdEvent2, updatedEvent)

            // When
            val createdCount = aggregateRoot.countEventsOfType(EntityCreated::class.java)
            val updatedCount = aggregateRoot.countEventsOfType(EntityUpdated::class.java)
            val deletedCount = aggregateRoot.countEventsOfType(EntityDeleted::class.java)

            // Then
            assertAll(
                { assertEquals(2, createdCount) },
                { assertEquals(1, updatedCount) },
                { assertEquals(0, deletedCount) }
            )
        }

        @Test
        @DisplayName("도메인 이벤트 제거 기능")
        fun `clear domain events functionality`() {
            // Given
            val aggregateRoot = TestAbstractAggregateRoot()
            val events = listOf(
                EntityCreated(
                    aggregateId = testAggregateId,
                    eventContext = testEventContext,
                    payload = EntityCreatedPayload("User", mapOf())
                ),
                EntityUpdated(
                    aggregateId = testAggregateId,
                    eventContext = testEventContext,
                    payload = EntityUpdatedPayload("User", mapOf(), mapOf(), setOf())
                )
            )
            aggregateRoot.addDomainEventsFromCollection(events)

            // When
            aggregateRoot.clearDomainEvents()

            // Then
            assertAll(
                { assertTrue(aggregateRoot.domainEvents.isEmpty()) },
                { assertFalse(aggregateRoot.hasDomainEvents()) }
            )
        }
    }

    @Nested
    @DisplayName("AggregateRoot 확장 함수 테스트")
    inner class AggregateRootExtensionTest {

        @Test
        @DisplayName("특정 타입의 도메인 이벤트 필터링")
        fun `filter domain events of specific type`() {
            // Given
            val aggregateRoot = TestAbstractAggregateRoot()
            val createdEvent = EntityCreated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload("User", mapOf())
            )
            val updatedEvent = EntityUpdated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityUpdatedPayload("User", mapOf(), mapOf(), setOf())
            )
            val stateChangedEvent = EntityStateChanged(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityStateChangedPayload("User", "OLD", "NEW")
            )

            aggregateRoot.addMultipleDomainEvents(createdEvent, updatedEvent, stateChangedEvent)

            // When
            val createdEvents = aggregateRoot.getDomainEventsOfType<EntityCreated>()
            val updatedEvents = aggregateRoot.getDomainEventsOfType<EntityUpdated>()
            val deletedEvents = aggregateRoot.getDomainEventsOfType<EntityDeleted>()

            // Then
            assertAll(
                { assertEquals(1, createdEvents.size) },
                { assertEquals(createdEvent, createdEvents.first()) },
                { assertEquals(1, updatedEvents.size) },
                { assertEquals(updatedEvent, updatedEvents.first()) },
                { assertTrue(deletedEvents.isEmpty()) }
            )
        }

        @Test
        @DisplayName("특정 타입의 도메인 이벤트 존재 여부 확인")
        fun `check if domain event of specific type exists`() {
            // Given
            val aggregateRoot = TestAbstractAggregateRoot()
            val createdEvent = EntityCreated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload("User", mapOf())
            )
            val updatedEvent = EntityUpdated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityUpdatedPayload("User", mapOf(), mapOf(), setOf())
            )

            aggregateRoot.addMultipleDomainEvents(createdEvent, updatedEvent)

            // When & Then
            assertAll(
                { assertTrue(aggregateRoot.hasDomainEventOfType<EntityCreated>()) },
                { assertTrue(aggregateRoot.hasDomainEventOfType<EntityUpdated>()) },
                { assertFalse(aggregateRoot.hasDomainEventOfType<EntityDeleted>()) },
                { assertFalse(aggregateRoot.hasDomainEventOfType<EntityStateChanged>()) }
            )
        }
    }

    @Nested
    @DisplayName("복잡한 시나리오 및 통합 테스트")
    inner class ComplexScenarioTest {

        @Test
        @DisplayName("실제 도메인 시나리오 - 사용자 생성 및 활성화")
        fun `real domain scenario - user creation and activation`() {
            // Given
            val aggregateRoot = TestAbstractAggregateRoot()
            val correlationId = UUID.randomUUID()

            // When - 사용자 생성
            val userCreatedEvent = EntityCreated(
                aggregateId = testAggregateId,
                correlationId = correlationId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload(
                    "User",
                    mapOf("name" to "John Doe", "email" to "john@example.com", "status" to "INACTIVE")
                )
            )
            aggregateRoot.addDomainEvent(userCreatedEvent)

            // 사용자 활성화
            val userActivatedEvent = userCreatedEvent.createFollowUpEvent { causationId, corrId ->
                EntityStateChanged(
                    aggregateId = testAggregateId,
                    causationId = causationId,
                    correlationId = corrId,
                    eventContext = testEventContext,
                    payload = EntityStateChangedPayload("User", "INACTIVE", "ACTIVE")
                )
            }
            aggregateRoot.addDomainEvent(userActivatedEvent)

            // 프로필 업데이트
            val profileUpdatedEvent = userActivatedEvent.createFollowUpEvent { causationId, corrId ->
                EntityUpdated(
                    aggregateId = testAggregateId,
                    causationId = causationId,
                    correlationId = corrId,
                    eventContext = testEventContext,
                    payload = EntityUpdatedPayload(
                        "User",
                        mapOf("profile" to "incomplete"),
                        mapOf("profile" to "complete", "lastLogin" to LocalDateTime.now()),
                        setOf("profile", "lastLogin")
                    )
                )
            }
            aggregateRoot.addDomainEvent(profileUpdatedEvent)

            // Then
            assertAll(
                { assertEquals(3, aggregateRoot.domainEvents.size) },
                { assertTrue(aggregateRoot.hasDomainEvents()) },
                { assertEquals(1, aggregateRoot.getDomainEventsOfType<EntityCreated>().size) },
                { assertEquals(1, aggregateRoot.getDomainEventsOfType<EntityStateChanged>().size) },
                { assertEquals(1, aggregateRoot.getDomainEventsOfType<EntityUpdated>().size) },
                { assertTrue(aggregateRoot.hasDomainEventOfType<EntityCreated>()) },
                { assertTrue(aggregateRoot.hasDomainEventOfType<EntityStateChanged>()) },
                { assertTrue(aggregateRoot.hasDomainEventOfType<EntityUpdated>()) },

                // 인과 관계 체인 확인
                { assertEquals(correlationId, userCreatedEvent.correlationId) },
                { assertEquals(correlationId, userActivatedEvent.correlationId) },
                { assertEquals(correlationId, profileUpdatedEvent.correlationId) },
                { assertNull(userCreatedEvent.causationId) },
                { assertEquals(userCreatedEvent.eventId, userActivatedEvent.causationId) },
                { assertEquals(userActivatedEvent.eventId, profileUpdatedEvent.causationId) }
            )
        }

        @Test
        @DisplayName("대량 이벤트 처리 성능 테스트")
        fun `bulk event processing performance test`() {
            // Given
            val aggregateRoot = TestAbstractAggregateRoot()

            // When & Then
            assertDoesNotThrow {
                val events = (1..1000).map { index ->
                    EntityCreated(
                        aggregateId = UUID.randomUUID(),
                        eventContext = testEventContext,
                        payload = EntityCreatedPayload("BulkEntity$index", mapOf("index" to index))
                    )
                }

                aggregateRoot.addDomainEventsFromCollection(events)

                // 기본 연산 수행
                aggregateRoot.hasDomainEvents()
                aggregateRoot.getDomainEventsOfType<EntityCreated>()
                aggregateRoot.hasDomainEventOfType<EntityCreated>()
                aggregateRoot.countEventsOfType(EntityCreated::class.java)

                assertEquals(1000, aggregateRoot.domainEvents.size)
                assertEquals(1000, aggregateRoot.countEventsOfType(EntityCreated::class.java))
            }
        }

        @Test
        @DisplayName("이벤트 제거 후 상태 확인")
        fun `state verification after event clearing`() {
            // Given
            val aggregateRoot = TestAbstractAggregateRoot()
            val events = (1..10).map { index ->
                EntityCreated(
                    aggregateId = UUID.randomUUID(),
                    eventContext = testEventContext,
                    payload = EntityCreatedPayload("Entity$index", mapOf())
                )
            }
            aggregateRoot.addDomainEventsFromCollection(events)

            // 이벤트가 있는 상태 확인
            assertTrue(aggregateRoot.hasDomainEvents())
            assertEquals(10, aggregateRoot.domainEvents.size)

            // When
            aggregateRoot.clearDomainEvents()

            // Then
            assertAll(
                { assertFalse(aggregateRoot.hasDomainEvents()) },
                { assertTrue(aggregateRoot.domainEvents.isEmpty()) },
                { assertFalse(aggregateRoot.hasDomainEventOfType<EntityCreated>()) },
                { assertEquals(0, aggregateRoot.countEventsOfType(EntityCreated::class.java)) },
                { assertTrue(aggregateRoot.getDomainEventsOfType<EntityCreated>().isEmpty()) }
            )
        }
    }

    @Nested
    @DisplayName("경계값 및 예외 상황 테스트")
    inner class EdgeCaseTest {

        @Test
        @DisplayName("빈 컬렉션으로 이벤트 추가")
        fun `add events with empty collection`() {
            // Given
            val aggregateRoot = TestAbstractAggregateRoot()
            val emptyEvents = emptyList<DomainEvent>()

            // When
            aggregateRoot.addDomainEventsFromCollection(emptyEvents)

            // Then
            assertAll(
                { assertTrue(aggregateRoot.domainEvents.isEmpty()) },
                { assertFalse(aggregateRoot.hasDomainEvents()) }
            )
        }

        @Test
        @DisplayName("이미 비어있는 상태에서 이벤트 제거")
        fun `clear events when already empty`() {
            // Given
            val aggregateRoot = TestAbstractAggregateRoot()

            // When
            aggregateRoot.clearDomainEvents()

            // Then
            assertAll(
                { assertTrue(aggregateRoot.domainEvents.isEmpty()) },
                { assertFalse(aggregateRoot.hasDomainEvents()) }
            )
        }

        @Test
        @DisplayName("null이 아닌 동일한 이벤트 여러 번 추가")
        fun `add same event multiple times`() {
            // Given
            val aggregateRoot = TestAbstractAggregateRoot()
            val event = EntityCreated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload("User", mapOf())
            )

            // When
            aggregateRoot.addDomainEvent(event)
            aggregateRoot.addDomainEvent(event)
            aggregateRoot.addDomainEvent(event)

            // Then
            assertAll(
                { assertEquals(3, aggregateRoot.domainEvents.size) },
                { assertTrue(aggregateRoot.domainEvents.all { it == event }) }
            )
        }
    }
}