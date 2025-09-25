package cloud.luigi99.blog.common.persistence

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

/**
 * JPA 기반 레포지토리의 공통 기반 인터페이스
 *
 * 모든 JPA 레포지토리에서 공통으로 사용되는 메서드들을 제공합니다.
 * - 기본 CRUD 연산 (Soft Delete 자동 적용)
 * - Soft Delete 지원 (BaseJpaEntity의 Hibernate 어노테이션 사용)
 * - 대량 연산 지원
 * - 감사 정보 기반 조회
 *
 * 주의: BaseJpaEntity의 @SoftDelete 어노테이션으로 인해 모든 조회에서
 * deleted=false 조건이 자동으로 적용됩니다.
 */
@NoRepositoryBean
interface JpaBaseRepository<T: BaseJpaEntity, ID : Any> : JpaRepository<T, ID> {

    /**
     * 특정 날짜 이후에 생성된 엔티티들을 조회합니다.
     *
     * @param createdAt 기준 생성 날짜
     * @return 엔티티 목록
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdAt >= :createdAt")
    fun findAllCreatedAfter(createdAt: LocalDateTime): List<T>

    /**
     * 특정 날짜 이후에 수정된 엔티티들을 조회합니다.
     *
     * @param updatedAt 기준 수정 날짜
     * @return 엔티티 목록
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.updatedAt >= :updatedAt")
    fun findAllUpdatedAfter(updatedAt: LocalDateTime): List<T>

    // === 낙관적 락 관련 메서드 ===

    /**
     * 락을 사용하여 엔티티를 조회합니다.
     *
     * @param id 조회할 엔티티 ID
     * @return 엔티티 (옵셔널)
     */
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id")
    fun findByIdWithLock(id: ID): Optional<T>

    /**
     * 특정 버전의 엔티티를 조회합니다.
     *
     * @param id 조회할 엔티티 ID
     * @param version 엔티티 버전
     * @return 엔티티 (옵셔널)
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.version = :version")
    fun findByIdAndVersion(id: ID, version: Long): Optional<T>

    // === Soft Delete 관련 메서드 ===
    // 주의: @SoftDelete 어노테이션으로 인해 JPA 기본 메서드들도 자동으로 deleted=false 조건 적용

    /**
     * 삭제된 엔티티를 복원합니다.
     *
     * @param id 복원할 엔티티 ID
     * @return 영향받은 엔티티 수
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE #{#entityName} e SET e.deleted = false, e.deletedAt = null WHERE e.id = :id")
    fun restoreById(id: ID): Int

    /**
     * 삭제된 엔티티들을 조회합니다.
     * @SoftDelete 어노테이션을 우회하여 직접 조건 명시
     *
     * @return 삭제된 엔티티 목록
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = true")
    fun findAllDeleted(): List<T>

    /**
     * 삭제 여부와 관계없이 모든 엔티티를 조회합니다.
     * @SoftDelete 어노테이션을 우회하여 직접 조건 명시
     *
     * @return 모든 엔티티 목록
     */
    @Query("SELECT e FROM #{#entityName} e")
    fun findAllIncludingDeleted(): List<T>
}