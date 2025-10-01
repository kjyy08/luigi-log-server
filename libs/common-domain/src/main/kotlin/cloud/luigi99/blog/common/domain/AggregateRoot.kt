package cloud.luigi99.blog.common.domain

/**
 * 애그리게이트 루트(Aggregate Root)의 기반이 되는 추상 클래스
 *
 * 개인 기술 블로그 프로젝트에서 사용되는 모든 애그리게이트 루트는 이 클래스를 상속받아
 * 도메인 이벤트 관리 기능과 트랜잭션 경계를 제공받습니다.
 *
 * 애그리게이트 루트는 다음 책임을 가집니다:
 * - 도메인 이벤트 수집 및 발행
 * - 비즈니스 불변 조건 보장
 * - 트랜잭션 경계 정의
 *
 * @param T 엔티티 식별자 타입 (ValueObject를 상속받은 타입)
 */
abstract class AggregateRoot<out T : ValueObject> : BaseEntity<T>() {

    /**
     * 발생한 도메인 이벤트들을 저장하는 내부 컬렉션
     */
    private val _domainEvents = mutableListOf<DomainEvent>()

    /**
     * 현재 애그리게이트에서 발생한 도메인 이벤트들을 읽기 전용으로 반환합니다.
     *
     * @return 도메인 이벤트 리스트 (읽기 전용)
     */
    val domainEvents: List<DomainEvent>
        get() = _domainEvents.toList()

    /**
     * 도메인 이벤트가 존재하는지 확인합니다.
     *
     * @return 도메인 이벤트가 하나 이상 있으면 true, 없으면 false
     */
    fun hasDomainEvents(): Boolean {
        return _domainEvents.isNotEmpty()
    }

    /**
     * 특정 타입의 도메인 이벤트가 존재하는지 확인합니다.
     *
     * @param eventType 확인할 이벤트 타입
     * @return 해당 타입의 이벤트가 존재하면 true, 없으면 false
     */
    fun hasDomainEvent(eventType: String): Boolean {
        return _domainEvents.any { it.eventType == eventType }
    }

    /**
     * 도메인 이벤트를 애그리게이트에 추가합니다.
     *
     * 비즈니스 로직 수행 중 발생한 중요한 도메인 이벤트를 기록하여
     * 나중에 이벤트 발행기를 통해 다른 바운디드 컨텍스트로 전파할 수 있습니다.
     *
     * @param event 추가할 도메인 이벤트
     */
    protected fun addDomainEvent(event: DomainEvent) {
        _domainEvents.add(event)
    }

    /**
     * 여러 도메인 이벤트를 한 번에 추가합니다.
     *
     * @param events 추가할 도메인 이벤트들
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
     * 저장된 모든 도메인 이벤트를 제거합니다.
     *
     * 일반적으로 이벤트 발행이 완료된 후 호출되어 메모리 누수를 방지합니다.
     * 이벤트 발행기에서 자동으로 호출되므로 직접 호출할 필요는 없습니다.
     */
    fun clearDomainEvents() {
        _domainEvents.clear()
    }

    /**
     * 특정 타입의 도메인 이벤트들만 제거합니다.
     *
     * @param eventType 제거할 이벤트 타입
     * @return 제거된 이벤트의 개수
     */
    fun clearDomainEvents(eventType: String): Int {
        val sizeBefore = _domainEvents.size
        _domainEvents.removeAll { it.eventType == eventType }
        return sizeBefore - _domainEvents.size
    }

    /**
     * 도메인 이벤트의 개수를 반환합니다.
     *
     * @return 현재 저장된 도메인 이벤트의 개수
     */
    fun domainEventCount(): Int {
        return _domainEvents.size
    }
}