package cloud.luigi99.blog.common.domain

import java.time.LocalDateTime
import java.util.*

/**
 * Sealed interface for domain events providing type-safe event hierarchy
 * with exhaustive when expressions and compile-time safety.
 *
 * This design supports:
 * - Type-safe event handling with exhaustive when expressions
 * - Event versioning and evolution
 * - Rich event metadata for tracing and debugging
 * - Event sourcing patterns
 * - Cross-cutting concerns like correlation and causation tracking
 */
sealed interface DomainEvent {
    /** Unique identifier for this event instance */
    val eventId: UUID

    /** Timestamp when the event occurred */
    val occurredAt: LocalDateTime

    /** ID of the aggregate that produced this event */
    val aggregateId: UUID

    /** Version of the event schema for evolution support */
    val eventVersion: Int

    /** Correlation ID for tracing related events across system boundaries */
    val correlationId: UUID?

    /** Causation ID pointing to the event that caused this event */
    val causationId: UUID?

    /** Context information about where/how this event was generated */
    val eventContext: EventContext

    /** Event payload containing the actual business data */
    val payload: EventPayload
}

/**
 * Context information for event tracing and debugging
 */
data class EventContext(
    /** Service or component that generated the event */
    val source: String,

    /** User or system that initiated the action */
    val initiatedBy: String?,

    /** Additional metadata for debugging and analysis */
    val metadata: Map<String, Any> = emptyMap()
)

/**
 * Base interface for event payloads
 */
sealed interface EventPayload

/**
 * Common entity lifecycle events
 */

/**
 * Event indicating an entity was created
 */
data class EntityCreated(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: LocalDateTime = LocalDateTime.now(),
    override val aggregateId: UUID,
    override val eventVersion: Int = 1,
    override val correlationId: UUID? = null,
    override val causationId: UUID? = null,
    override val eventContext: EventContext,
    override val payload: EntityCreatedPayload
) : DomainEvent

data class EntityCreatedPayload(
    val entityType: String,
    val entityData: Map<String, Any>
) : EventPayload

/**
 * Event indicating an entity was updated
 */
data class EntityUpdated(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: LocalDateTime = LocalDateTime.now(),
    override val aggregateId: UUID,
    override val eventVersion: Int = 1,
    override val correlationId: UUID? = null,
    override val causationId: UUID? = null,
    override val eventContext: EventContext,
    override val payload: EntityUpdatedPayload
) : DomainEvent

data class EntityUpdatedPayload(
    val entityType: String,
    val previousData: Map<String, Any>,
    val updatedData: Map<String, Any>,
    val changedFields: Set<String>
) : EventPayload

/**
 * Event indicating an entity was deleted
 */
data class EntityDeleted(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: LocalDateTime = LocalDateTime.now(),
    override val aggregateId: UUID,
    override val eventVersion: Int = 1,
    override val correlationId: UUID? = null,
    override val causationId: UUID? = null,
    override val eventContext: EventContext,
    override val payload: EntityDeletedPayload
) : DomainEvent

data class EntityDeletedPayload(
    val entityType: String,
    val deletedData: Map<String, Any>,
    val deletionReason: String?
) : EventPayload

/**
 * Event indicating an entity's state changed
 */
data class EntityStateChanged(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: LocalDateTime = LocalDateTime.now(),
    override val aggregateId: UUID,
    override val eventVersion: Int = 1,
    override val correlationId: UUID? = null,
    override val causationId: UUID? = null,
    override val eventContext: EventContext,
    override val payload: EntityStateChangedPayload
) : DomainEvent

data class EntityStateChangedPayload(
    val entityType: String,
    val previousState: String,
    val newState: String,
    val transitionMetadata: Map<String, Any> = emptyMap()
) : EventPayload

/**
 * Utility functions for domain events
 */
object DomainEventUtils {

    /**
     * Extract event metadata for logging and debugging
     */
    fun extractMetadata(event: DomainEvent): Map<String, Any> = mapOf(
        "eventId" to event.eventId,
        "eventType" to event::class.simpleName!!,
        "occurredAt" to event.occurredAt,
        "aggregateId" to event.aggregateId,
        "eventVersion" to event.eventVersion,
        "source" to event.eventContext.source,
        "correlationId" to (event.correlationId ?: "none"),
        "causationId" to (event.causationId ?: "none")
    )

    /**
     * Check if an event is part of a correlation chain
     */
    fun isCorrelated(event: DomainEvent): Boolean = event.correlationId != null

    /**
     * Check if an event was caused by another event
     */
    fun hasCausation(event: DomainEvent): Boolean = event.causationId != null

    /**
     * Extract the entity type from common event payloads
     */
    fun extractEntityType(event: DomainEvent): String? = when (val payload = event.payload) {
        is EntityCreatedPayload -> payload.entityType
        is EntityUpdatedPayload -> payload.entityType
        is EntityDeletedPayload -> payload.entityType
        is EntityStateChangedPayload -> payload.entityType
    }

    /**
     * Validate event structure
     */
    fun validate(event: DomainEvent): List<String> {
        val errors = mutableListOf<String>()

        if (event.eventVersion <= 0) {
            errors.add("Event version must be positive")
        }

        if (event.eventContext.source.isBlank()) {
            errors.add("Event context source cannot be blank")
        }

        if (event.causationId == event.eventId) {
            errors.add("Event cannot be its own cause")
        }

        return errors
    }
}

/**
 * Extension functions for enhanced event handling
 */

/**
 * Check if this event matches a specific entity type
 */
fun DomainEvent.isForEntityType(entityType: String): Boolean =
    DomainEventUtils.extractEntityType(this) == entityType

/**
 * Create a follow-up event with proper causation chain
 */
fun DomainEvent.createFollowUpEvent(
    eventFactory: (causationId: UUID, correlationId: UUID?) -> DomainEvent
): DomainEvent = eventFactory(this.eventId, this.correlationId ?: this.eventId)

/**
 * Convert event to a serializable map for event streaming
 */
fun DomainEvent.toSerializableMap(): Map<String, Any> = mapOf(
    "eventId" to eventId.toString(),
    "eventType" to this::class.simpleName!!,
    "occurredAt" to occurredAt.toString(),
    "aggregateId" to aggregateId.toString(),
    "eventVersion" to eventVersion,
    "correlationId" to (correlationId?.toString() ?: ""),
    "causationId" to (causationId?.toString() ?: ""),
    "eventContext" to mapOf(
        "source" to eventContext.source,
        "initiatedBy" to (eventContext.initiatedBy ?: ""),
        "metadata" to eventContext.metadata
    ),
    "payload" to when (val p = payload) {
        is EntityCreatedPayload -> mapOf(
            "entityType" to p.entityType,
            "entityData" to p.entityData
        )
        is EntityUpdatedPayload -> mapOf(
            "entityType" to p.entityType,
            "previousData" to p.previousData,
            "updatedData" to p.updatedData,
            "changedFields" to p.changedFields
        )
        is EntityDeletedPayload -> mapOf(
            "entityType" to p.entityType,
            "deletedData" to p.deletedData,
            "deletionReason" to (p.deletionReason ?: "")
        )
        is EntityStateChangedPayload -> mapOf(
            "entityType" to p.entityType,
            "previousState" to p.previousState,
            "newState" to p.newState,
            "transitionMetadata" to p.transitionMetadata
        )
    }
)