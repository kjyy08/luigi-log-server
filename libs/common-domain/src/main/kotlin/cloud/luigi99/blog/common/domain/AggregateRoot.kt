package cloud.luigi99.blog.common.domain

/**
 * 도메인 이벤트를 발행할 수 있는 애그리게이트 루트의 계약을 정의하는 인터페이스
 *
 * DDD(Domain Driven Design)에서 애그리게이트 루트는 도메인 이벤트를
 * 발행할 수 있어야 하며, 이 인터페이스는 그 계약을 정의합니다.
 *
 * @author Luigi Blog Platform
 * @since 1.0
 */
interface AggregateRoot : BaseEntity {
    /**
     * 현재 애그리게이트에서 발생한 도메인 이벤트 목록
     */
    val domainEvents: List<DomainEvent>

    /**
     * 도메인 이벤트를 추가합니다.
     *
     * @param event 추가할 도메인 이벤트
     */
    fun addDomainEvent(event: DomainEvent)

    /**
     * 모든 도메인 이벤트를 제거합니다.
     * 일반적으로 이벤트 발행 후에 호출됩니다.
     */
    fun clearDomainEvents()
}

/**
 * AggregateRoot 인터페이스의 기본 구현을 제공하는 추상 클래스
 *
 * 도메인 이벤트 관리 기능을 포함하며, 구체적인 애그리게이트 루트
 * 클래스에서 상속받아 사용할 수 있습니다.
 *
 * @author Luigi Blog Platform
 * @since 1.0
 */
abstract class AbstractAggregateRoot : AggregateRoot {
    private val _domainEvents = mutableListOf<DomainEvent>()

    override val domainEvents: List<DomainEvent>
        get() = _domainEvents.toList()

    override fun addDomainEvent(event: DomainEvent) {
        _domainEvents.add(event)
    }

    override fun clearDomainEvents() {
        _domainEvents.clear()
    }

    /**
     * 여러 도메인 이벤트를 한 번에 추가합니다.
     *
     * @param events 추가할 도메인 이벤트 목록
     */
    protected fun addDomainEvents(vararg events: DomainEvent) {
        _domainEvents.addAll(events)
    }

    /**
     * 여러 도메인 이벤트를 한 번에 추가합니다.
     *
     * @param events 추가할 도메인 이벤트 컬렉션
     */
    protected fun addDomainEvents(events: Collection<DomainEvent>) {
        _domainEvents.addAll(events)
    }

    /**
     * 도메인 이벤트가 있는지 확인합니다.
     *
     * @return 도메인 이벤트가 하나 이상 있으면 true
     */
    fun hasDomainEvents(): Boolean = _domainEvents.isNotEmpty()

    /**
     * 특정 타입의 도메인 이벤트 개수를 반환합니다.
     *
     * @param eventType 확인할 이벤트 타입
     * @return 해당 타입의 이벤트 개수
     */
    fun countDomainEventsOfType(eventType: Class<out DomainEvent>): Int =
        _domainEvents.count { eventType.isInstance(it) }
}

/**
 * AggregateRoot 확장 함수들
 */

/**
 * 특정 타입의 도메인 이벤트를 필터링하여 반환합니다.
 */
inline fun <reified T : DomainEvent> AggregateRoot.getDomainEventsOfType(): List<T> =
    domainEvents.filterIsInstance<T>()

/**
 * 특정 타입의 도메인 이벤트가 있는지 확인합니다.
 */
inline fun <reified T : DomainEvent> AggregateRoot.hasDomainEventOfType(): Boolean =
    domainEvents.any { it is T }