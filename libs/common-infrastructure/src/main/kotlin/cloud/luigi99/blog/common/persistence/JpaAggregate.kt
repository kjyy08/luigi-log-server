package cloud.luigi99.blog.common.persistence

import cloud.luigi99.blog.common.domain.AggregateRoot
import cloud.luigi99.blog.common.domain.ValueObject
import jakarta.persistence.*
import org.hibernate.annotations.SoftDelete
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * JPA 기반 애그리게이트 루트의 기반이 되는 추상 클래스
 *
 * common-domain의 AggregateRoot를 상속받아 JPA 환경에서
 * 애그리게이트 패턴을 구현할 수 있도록 지원합니다.
 *
 * 주요 기능:
 * - 도메인 이벤트 관리 (AggregateRoot에서 상속)
 * - JPA Auditing을 통한 자동 생성/수정 시각 관리
 * - Soft Delete 자동 적용 (Hibernate 6.4+ @SoftDelete)
 * - Persistable 인터페이스로 JPA의 새 엔티티 감지 최적화
 * - 트랜잭션 경계와 비즈니스 불변 조건 보장
 *
 * Hibernate @SoftDelete 어노테이션 사용:
 * - Hibernate가 자동으로 deleted 컬럼을 관리
 * - SELECT 쿼리에 자동으로 WHERE deleted = false 조건 추가
 * - DELETE 시 UPDATE deleted = true로 자동 변환
 * - 별도의 deleted 필드 선언 불필요 (Hibernate가 자동 생성)
 *
 * @param T 엔티티 식별자 타입 (ValueObject를 상속받은 타입)
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
@SoftDelete(columnName = "deleted")
abstract class JpaAggregate<T : ValueObject> : AggregateRoot<T>(), Persistable<T> {

    /**
     * 엔티티 생성 시각
     * JPA Auditing을 통해 자동으로 설정됩니다.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    override val createdAt: LocalDateTime = LocalDateTime.now()

    /**
     * 엔티티 최종 수정 시각
     * JPA Auditing을 통해 자동으로 관리됩니다.
     */
    @LastModifiedDate
    @Column(nullable = true)
    override var updatedAt: LocalDateTime? = null

    /**
     * 낙관적 락을 위한 버전 필드
     * 엔티티가 수정될 때마다 자동으로 증가됩니다.
     */
    @Version
    @Column(nullable = false)
    var version: Long = 0L
        protected set

    /**
     * 삭제 시각
     * Soft Delete 수행 시 자동으로 설정됩니다.
     *
     * 주의: Hibernate @SoftDelete는 deleted 플래그만 관리하므로,
     * deletedAt은 애플리케이션 레벨에서 관리해야 합니다.
     */
    @Column(nullable = true)
    var deletedAt: LocalDateTime? = null
        protected set

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

    /**
     * JPA 삭제 이벤트 이전에 실행되는 콜백 메서드
     *
     * Hibernate @SoftDelete가 deleted 플래그를 자동으로 true로 설정하므로,
     * 여기서는 deletedAt 시각만 기록합니다.
     *
     * @PreRemove 어노테이션으로 JPA 컨테이너가 자동 호출합니다.
     */
    @PreRemove
    fun onSoftDelete() {
        this.deletedAt = LocalDateTime.now()
    }
}