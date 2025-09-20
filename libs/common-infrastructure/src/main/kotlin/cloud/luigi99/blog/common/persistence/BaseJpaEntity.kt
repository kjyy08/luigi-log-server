package cloud.luigi99.blog.common.persistence

import cloud.luigi99.blog.common.domain.BaseEntity
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

/**
 * JPA 엔티티의 공통 기반 클래스
 *
 * common-domain의 BaseEntity와 연동하여 JPA 특화 기능을 제공합니다.
 * - JPA Auditing을 통한 자동 생성/수정 시각 관리
 * - UUID 기반 기본키 매핑
 * - 도메인 엔티티와의 일관성 보장
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseJpaEntity : BaseEntity() {

    /**
     * 엔티티의 고유 식별자
     * UUID를 사용하여 전역적으로 유니크한 값을 보장합니다.
     */
    @Id
    @Column(columnDefinition = "BINARY(16)")
    override val id: UUID = UUID.randomUUID()

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
    override val updatedAt: LocalDateTime? = null
}