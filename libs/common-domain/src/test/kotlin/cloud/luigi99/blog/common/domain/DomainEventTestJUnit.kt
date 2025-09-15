package cloud.luigi99.blog.common.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import java.time.LocalDateTime
import java.util.*
import kotlin.test.*

@DisplayName("DomainEvent 및 관련 클래스 테스트")
class DomainEventTestJUnit {

    private val testAggregateId = UUID.randomUUID()
    private val testEventContext = EventContext(
        source = "test-service",
        initiatedBy = "test-user",
        metadata = mapOf("key" to "value")
    )

    @Nested
    @DisplayName("EntityCreated 이벤트 테스트")
    inner class EntityCreatedEventTest {

        @Test
        @DisplayName("EntityCreated 이벤트 생성 및 기본값 확인")
        fun `EntityCreated event creation and default values`() {
            // Given
            val payload = EntityCreatedPayload(
                entityType = "User",
                entityData = mapOf("id" to testAggregateId, "name" to "John Doe")
            )

            // When
            val event = EntityCreated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = payload
            )

            // Then
            assertAll(
                { assertNotNull(event.eventId) },
                { assertNotNull(event.occurredAt) },
                { assertEquals(testAggregateId, event.aggregateId) },
                { assertEquals(1, event.eventVersion) },
                { assertNull(event.correlationId) },
                { assertNull(event.causationId) },
                { assertEquals(testEventContext, event.eventContext) },
                { assertEquals(payload, event.payload) }
            )
        }

        @Test
        @DisplayName("EntityCreated 이벤트의 상관관계 설정")
        fun `EntityCreated event with correlation and causation`() {
            // Given
            val correlationId = UUID.randomUUID()
            val causationId = UUID.randomUUID()
            val payload = EntityCreatedPayload("User", mapOf("id" to testAggregateId))

            // When
            val event = EntityCreated(
                aggregateId = testAggregateId,
                eventVersion = 2,
                correlationId = correlationId,
                causationId = causationId,
                eventContext = testEventContext,
                payload = payload
            )

            // Then
            assertAll(
                { assertEquals(correlationId, event.correlationId) },
                { assertEquals(causationId, event.causationId) },
                { assertEquals(2, event.eventVersion) }
            )
        }
    }

    @Nested
    @DisplayName("EntityUpdated 이벤트 테스트")
    inner class EntityUpdatedEventTest {

        @Test
        @DisplayName("EntityUpdated 이벤트 생성 및 변경 필드 추적")
        fun `EntityUpdated event creation and changed fields tracking`() {
            // Given
            val previousData = mapOf("name" to "John", "age" to 25, "status" to "INACTIVE")
            val updatedData = mapOf("name" to "John Doe", "age" to 26, "status" to "ACTIVE")
            val changedFields = setOf("name", "age", "status")

            val payload = EntityUpdatedPayload(
                entityType = "User",
                previousData = previousData,
                updatedData = updatedData,
                changedFields = changedFields
            )

            // When
            val event = EntityUpdated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = payload
            )

            // Then
            assertAll(
                { assertEquals("User", event.payload.entityType) },
                { assertEquals(previousData, event.payload.previousData) },
                { assertEquals(updatedData, event.payload.updatedData) },
                { assertEquals(changedFields, event.payload.changedFields) },
                { assertTrue(event.payload.changedFields.contains("name")) },
                { assertTrue(event.payload.changedFields.contains("status")) }
            )
        }
    }

    @Nested
    @DisplayName("EntityDeleted 이벤트 테스트")
    inner class EntityDeletedEventTest {

        @Test
        @DisplayName("EntityDeleted 이벤트 생성 및 삭제 사유 확인")
        fun `EntityDeleted event creation and deletion reason`() {
            // Given
            val deletedData = mapOf("id" to testAggregateId, "name" to "Deleted Entity")
            val deletionReason = "User requested account deletion"

            val payload = EntityDeletedPayload(
                entityType = "User",
                deletedData = deletedData,
                deletionReason = deletionReason
            )

            // When
            val event = EntityDeleted(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = payload
            )

            // Then
            assertAll(
                { assertEquals("User", event.payload.entityType) },
                { assertEquals(deletedData, event.payload.deletedData) },
                { assertEquals(deletionReason, event.payload.deletionReason) }
            )
        }
    }

    @Nested
    @DisplayName("EntityStateChanged 이벤트 테스트")
    inner class EntityStateChangedEventTest {

        @Test
        @DisplayName("EntityStateChanged 이벤트 생성 및 상태 전환 확인")
        fun `EntityStateChanged event creation and state transition`() {
            // Given
            val transitionMetadata = mapOf(
                "timestamp" to LocalDateTime.now(),
                "triggeredBy" to "automated-process",
                "reason" to "Payment completed"
            )

            val payload = EntityStateChangedPayload(
                entityType = "Order",
                previousState = "PENDING",
                newState = "CONFIRMED",
                transitionMetadata = transitionMetadata
            )

            // When
            val event = EntityStateChanged(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = payload
            )

            // Then
            assertAll(
                { assertEquals("Order", event.payload.entityType) },
                { assertEquals("PENDING", event.payload.previousState) },
                { assertEquals("CONFIRMED", event.payload.newState) },
                { assertEquals(transitionMetadata, event.payload.transitionMetadata) }
            )
        }
    }

    @Nested
    @DisplayName("DomainEventUtils 유틸리티 테스트")
    inner class DomainEventUtilsTest {

        private fun createTestEvent(): EntityCreated {
            return EntityCreated(
                aggregateId = testAggregateId,
                correlationId = UUID.randomUUID(),
                causationId = UUID.randomUUID(),
                eventContext = testEventContext,
                payload = EntityCreatedPayload("User", mapOf("id" to testAggregateId))
            )
        }

        @Test
        @DisplayName("extractMetadata 기능 확인")
        fun `extractMetadata functionality`() {
            // Given
            val event = createTestEvent()

            // When
            val metadata = DomainEventUtils.extractMetadata(event)

            // Then
            assertAll(
                { assertEquals(event.eventId, metadata["eventId"]) },
                { assertEquals("EntityCreated", metadata["eventType"]) },
                { assertEquals(event.occurredAt, metadata["occurredAt"]) },
                { assertEquals(event.aggregateId, metadata["aggregateId"]) },
                { assertEquals(event.eventVersion, metadata["eventVersion"]) },
                { assertEquals(event.eventContext.source, metadata["source"]) },
                { assertEquals(event.correlationId, metadata["correlationId"]) },
                { assertEquals(event.causationId, metadata["causationId"]) }
            )
        }

        @Test
        @DisplayName("상관관계 및 인과관계 확인 기능")
        fun `correlation and causation check functionality`() {
            // Given
            val correlatedEvent = createTestEvent()
            val nonCorrelatedEvent = EntityCreated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload("User", mapOf())
            )

            // When & Then
            assertAll(
                { assertTrue(DomainEventUtils.isCorrelated(correlatedEvent)) },
                { assertFalse(DomainEventUtils.isCorrelated(nonCorrelatedEvent)) },
                { assertTrue(DomainEventUtils.hasCausation(correlatedEvent)) },
                { assertFalse(DomainEventUtils.hasCausation(nonCorrelatedEvent)) }
            )
        }

        @Test
        @DisplayName("엔티티 타입 추출 기능")
        fun `entity type extraction functionality`() {
            // Given
            val createdEvent = EntityCreated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload("User", mapOf())
            )
            val updatedEvent = EntityUpdated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityUpdatedPayload("Product", mapOf(), mapOf(), setOf())
            )

            // When & Then
            assertAll(
                { assertEquals("User", DomainEventUtils.extractEntityType(createdEvent)) },
                { assertEquals("Product", DomainEventUtils.extractEntityType(updatedEvent)) }
            )
        }

        @Test
        @DisplayName("이벤트 구조 검증 기능")
        fun `event structure validation functionality`() {
            // Given
            val validEvent = createTestEvent()
            val invalidVersionEvent = validEvent.copy(eventVersion = 0)
            val invalidSourceEvent = validEvent.copy(
                eventContext = testEventContext.copy(source = "")
            )
            val selfCausedEvent = validEvent.copy(causationId = validEvent.eventId)

            // When
            val validErrors = DomainEventUtils.validate(validEvent)
            val versionErrors = DomainEventUtils.validate(invalidVersionEvent)
            val sourceErrors = DomainEventUtils.validate(invalidSourceEvent)
            val selfCausedErrors = DomainEventUtils.validate(selfCausedEvent)

            // Then
            assertAll(
                { assertTrue(validErrors.isEmpty()) },
                { assertTrue(versionErrors.any { it.contains("version") }) },
                { assertTrue(sourceErrors.any { it.contains("source") }) },
                { assertTrue(selfCausedErrors.any { it.contains("cause") }) }
            )
        }
    }

    @Nested
    @DisplayName("DomainEvent 확장 함수 테스트")
    inner class DomainEventExtensionTest {

        @Test
        @DisplayName("isForEntityType 확장 함수")
        fun `isForEntityType extension function`() {
            // Given
            val userEvent = EntityCreated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload("User", mapOf())
            )
            val productEvent = EntityCreated(
                aggregateId = testAggregateId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload("Product", mapOf())
            )

            // When & Then
            assertAll(
                { assertTrue(userEvent.isForEntityType("User")) },
                { assertFalse(userEvent.isForEntityType("Product")) },
                { assertTrue(productEvent.isForEntityType("Product")) },
                { assertFalse(productEvent.isForEntityType("User")) }
            )
        }

        @Test
        @DisplayName("createFollowUpEvent 확장 함수")
        fun `createFollowUpEvent extension function`() {
            // Given
            val originalEvent = EntityCreated(
                aggregateId = testAggregateId,
                correlationId = UUID.randomUUID(),
                eventContext = testEventContext,
                payload = EntityCreatedPayload("User", mapOf())
            )

            // When
            val followUpEvent = originalEvent.createFollowUpEvent { causationId, correlationId ->
                EntityUpdated(
                    aggregateId = testAggregateId,
                    causationId = causationId,
                    correlationId = correlationId,
                    eventContext = testEventContext,
                    payload = EntityUpdatedPayload("User", mapOf(), mapOf(), setOf())
                )
            }

            // Then
            assertAll(
                { assertEquals(originalEvent.eventId, followUpEvent.causationId) },
                { assertEquals(originalEvent.correlationId, followUpEvent.correlationId) }
            )
        }

        @Test
        @DisplayName("toSerializableMap 확장 함수")
        fun `toSerializableMap extension function`() {
            // Given
            val event = EntityCreated(
                aggregateId = testAggregateId,
                correlationId = UUID.randomUUID(),
                causationId = UUID.randomUUID(),
                eventContext = testEventContext,
                payload = EntityCreatedPayload("User", mapOf("name" to "John"))
            )

            // When
            val serializedMap = event.toSerializableMap()

            // Then
            assertAll(
                { assertTrue(serializedMap.containsKey("eventId")) },
                { assertEquals("EntityCreated", serializedMap["eventType"]) },
                { assertTrue(serializedMap.containsKey("occurredAt")) },
                { assertTrue(serializedMap.containsKey("aggregateId")) },
                { assertEquals(1, serializedMap["eventVersion"]) },
                { assertTrue(serializedMap.containsKey("eventContext")) },
                { assertTrue(serializedMap.containsKey("payload")) }
            )
        }
    }

    @Nested
    @DisplayName("이벤트 생명주기 시나리오 테스트")
    inner class EventLifecycleScenarioTest {

        @Test
        @DisplayName("완전한 엔티티 생명주기 이벤트 체인 테스트")
        fun `complete entity lifecycle event chain test`() {
            // Given
            val correlationId = UUID.randomUUID()
            val entityData = mapOf("name" to "Test Entity", "status" to "DRAFT")

            // When - 엔티티 생성
            val createdEvent = EntityCreated(
                aggregateId = testAggregateId,
                correlationId = correlationId,
                eventContext = testEventContext,
                payload = EntityCreatedPayload("Article", entityData)
            )

            // 상태 변경
            val stateChangedEvent = createdEvent.createFollowUpEvent { causationId, corrId ->
                EntityStateChanged(
                    aggregateId = testAggregateId,
                    causationId = causationId,
                    correlationId = corrId,
                    eventContext = testEventContext,
                    payload = EntityStateChangedPayload("Article", "DRAFT", "PUBLISHED")
                )
            }

            // 엔티티 업데이트
            val updatedEvent = stateChangedEvent.createFollowUpEvent { causationId, corrId ->
                EntityUpdated(
                    aggregateId = testAggregateId,
                    causationId = causationId,
                    correlationId = corrId,
                    eventContext = testEventContext,
                    payload = EntityUpdatedPayload(
                        "Article",
                        mapOf("title" to "Old Title"),
                        mapOf("title" to "New Title"),
                        setOf("title")
                    )
                )
            }

            // 엔티티 삭제
            val deletedEvent = updatedEvent.createFollowUpEvent { causationId, corrId ->
                EntityDeleted(
                    aggregateId = testAggregateId,
                    causationId = causationId,
                    correlationId = corrId,
                    eventContext = testEventContext,
                    payload = EntityDeletedPayload("Article", mapOf(), "User requested deletion")
                )
            }

            // Then - 인과 관계 체인 확인
            assertAll(
                { assertEquals(correlationId, createdEvent.correlationId) },
                { assertEquals(correlationId, stateChangedEvent.correlationId) },
                { assertEquals(correlationId, updatedEvent.correlationId) },
                { assertEquals(correlationId, deletedEvent.correlationId) },

                { assertNull(createdEvent.causationId) },
                { assertEquals(createdEvent.eventId, stateChangedEvent.causationId) },
                { assertEquals(stateChangedEvent.eventId, updatedEvent.causationId) },
                { assertEquals(updatedEvent.eventId, deletedEvent.causationId) },

                { assertTrue(createdEvent.isForEntityType("Article")) },
                { assertTrue(stateChangedEvent.isForEntityType("Article")) },
                { assertTrue(updatedEvent.isForEntityType("Article")) },
                { assertTrue(deletedEvent.isForEntityType("Article")) }
            )
        }
    }
}