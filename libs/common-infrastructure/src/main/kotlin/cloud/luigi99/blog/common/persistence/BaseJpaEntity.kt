package cloud.luigi99.blog.common.persistence

import cloud.luigi99.blog.common.domain.BaseEntity
import cloud.luigi99.blog.common.domain.ValueObject
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SoftDelete
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * JPA 엔티티의 공통 기반 클래스
 *
 * common-domain의 BaseEntity와 연동하여 JPA 특화 기능을 제공합니다.
 * - JPA Auditing을 통한 자동 생성/수정 시각 관리
 * - UUID 기반 기본키 매핑
 * - 도메인 엔티티와의 일관성 보장
 * - Soft Delete 자동 적용 (Hibernate 어노테이션 사용)
 *
 * @param T 엔티티 식별자 타입 (ValueObject를 상속받은 타입)
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
@SoftDelete(columnName = "deleted")
@SQLDelete(sql = "UPDATE #{#entityName} SET deleted = true, deleted_at = NOW() WHERE id = ?")
abstract class BaseJpaEntity<out T : ValueObject> : BaseEntity<T>() {

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
     * Soft Delete를 위한 삭제 플래그
     * 물리적 삭제 대신 논리적 삭제를 지원합니다.
     */
    @Column(nullable = false)
    var deleted: Boolean = false
        protected set

    /**
     * 삭제 시각
     * Soft Delete 수행 시 설정됩니다.
     */
    @Column(nullable = true)
    var deletedAt: LocalDateTime? = null
        protected set


    /**
     * JPA 삭제 이벤트 이전에 실행되는 콜백 메서드
     * @PreRemove 어노테이션으로 JPA 컨테이너가 자동 호출합니다.
     */
    @PreRemove
    fun onSoftDelete() {
        this.deleted = true
        this.deletedAt = LocalDateTime.now()
    }


    /**
     * 엔티티가 삭제되었는지 확인합니다.
     */
    fun isDeleted(): Boolean = deleted
}