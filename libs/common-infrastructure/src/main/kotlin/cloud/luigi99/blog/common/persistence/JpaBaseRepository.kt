package cloud.luigi99.blog.common.persistence

import org.springframework.data.jpa.repository.JpaRepository
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
 * - 기본 CRUD 연산
 * - Soft Delete 지원
 * - 대량 연산 지원
 * - 감사 정보 기반 조회
 */
@NoRepositoryBean
interface JpaBaseRepository<T, ID : Any> : JpaRepository<T, ID> {

    /**
     * ID 목록으로 엔티티들을 조회합니다.
     *
     * @param ids 조회할 ID 목록
     * @return 조회된 엔티티 목록
     */
    fun findAllByIdIn(ids: Collection<ID>): List<T>

    /**
     * 특정 날짜 이후에 생성된 엔티티들을 조회합니다.
     *
     * @param createdAt 기준 생성 날짜
     * @return 조회된 엔티티 목록
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdAt >= :createdAt")
    fun findAllCreatedAfter(createdAt: LocalDateTime): List<T>

    /**
     * 특정 날짜 이후에 수정된 엔티티들을 조회합니다.
     *
     * @param updatedAt 기준 수정 날짜
     * @return 조회된 엔티티 목록
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.updatedAt >= :updatedAt")
    fun findAllUpdatedAfter(updatedAt: LocalDateTime): List<T>

    /**
     * ID 목록에 해당하는 엔티티들을 일괄 삭제합니다.
     *
     * @param ids 삭제할 ID 목록
     * @return 삭제된 엔티티 수
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM #{#entityName} e WHERE e.id IN :ids")
    fun deleteAllByIdIn(ids: Collection<ID>): Int

    /**
     * 특정 날짜 이전에 생성된 엔티티들을 일괄 삭제합니다.
     *
     * @param createdAt 기준 생성 날짜
     * @return 삭제된 엔티티 수
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM #{#entityName} e WHERE e.createdAt < :createdAt")
    fun deleteAllCreatedBefore(createdAt: LocalDateTime): Int

    /**
     * 엔티티의 존재 여부를 ID 목록으로 확인합니다.
     *
     * @param ids 확인할 ID 목록
     * @return 존재하는 ID들의 Map (ID -> true)
     */
    @Query("SELECT e.id FROM #{#entityName} e WHERE e.id IN :ids")
    fun findExistingIds(ids: Collection<ID>): List<ID>

    /**
     * 전체 엔티티 수를 반환합니다.
     *
     * @return 엔티티 총 개수
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e")
    fun countAll(): Long
}