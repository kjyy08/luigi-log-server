package cloud.luigi99.blog.common.persistence

import cloud.luigi99.blog.common.domain.DomainEvent
import cloud.luigi99.blog.common.domain.ValueObject
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import org.springframework.data.domain.Persistable
import java.util.*

/**
 * JPA 기반 애그리게이트 루트의 기반이 되는 추상 클래스
 *
 * common-domain의 AggregateRoot와 BaseJpaEntity를 결합하여 JPA 환경에서
 * 애그리게이트 패턴을 구현할 수 있도록 지원합니다.
 *
 * 주요 기능:
 * - 도메인 이벤트 관리 (AggregateRoot 상속)
 * - JPA Auditing 및 Soft Delete 지원 (BaseJpaEntity 상속)
 * - Persistable 인터페이스로 JPA의 새 엔티티 감지 최적화
 * - 트랜잭션 경계와 비즈니스 불변 조건 보장
 *
 * 실용적 접근:
 * BaseJpaEntity를 상속받아 JPA 특화 기능을 재사용하고,
 * AggregateRoot의 도메인 이벤트 관리 기능을 조합으로 구현합니다.
 *
 * @param T 엔티티 식별자 타입 (ValueObject를 상속받은 타입)
 */
@MappedSuperclass
abstract class JpaAggregate<T : ValueObject> : BaseJpaEntity<T>(), Persistable<T> {

    /**
     * 애그리게이트 루트의 도메인 이벤트 관리 기능
     * AggregateRoot와 동일한 기능을 조합(Composition)으로 제공합니다.
     */
    /**
     * 발생한 도메인 이벤트들을 저장하는 내부 컬렉션
     */
    @Transient
    private val _domainEvents = mutableListOf<DomainEvent>()

    /**
     * Persistable 인터페이스 구현: 엔티티 식별자 반환
     *
     * JPA가 엔티티의 식별자를 올바르게 인식할 수 있도록 entityId를 직접 반환합니다.
     * 제네릭 타입 T를 그대로 활용하여 타입 안전성을 보장합니다.
     *
     * @return T 타입의 엔티티 식별자
     */
    override fun getId(): T = entityId

    /**
     * Persistable 인터페이스 구현: 새 엔티티 여부 판단
     *
     * JPA의 persist vs merge 최적화를 위해 새 엔티티 여부를 판단합니다.
     * createdAt과 updatedAt이 같으면(또는 updatedAt이 null이면) 새 엔티티로 간주합니다.
     *
     * @return 새 엔티티이면 true, 기존 엔티티이면 false
     */
    override fun isNew(): Boolean = updatedAt?.equals(createdAt) != false

    // ========================================
    // 도메인 이벤트 관리 기능 (위임)
    // ========================================

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