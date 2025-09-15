package cloud.luigi99.blog.common.domain

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * 도메인 엔티티의 공통 계약을 정의하는 기본 인터페이스
 *
 * 모든 도메인 엔티티는 이 인터페이스를 구현해야 하며,
 * JPA Auditing과 호환되는 구조를 제공합니다.
 *
 * @author Luigi Blog Platform
 * @since 1.0
 */
interface BaseEntity {
    /**
     * 엔티티의 고유 식별자
     */
    val id: UUID

    /**
     * 엔티티 생성 시점
     * JPA @CreatedDate 어노테이션과 호환
     */
    val createdAt: LocalDateTime

    /**
     * 엔티티 최종 수정 시점
     * JPA @LastModifiedDate 어노테이션과 호환
     */
    val updatedAt: LocalDateTime?

    /**
     * 새로 생성된 엔티티인지 확인
     *
     * @return updatedAt이 null이면 true, 그렇지 않으면 false
     */
    fun isNew(): Boolean = updatedAt == null

    /**
     * 엔티티 생성 후 경과 시간 계산
     *
     * @param unit 시간 단위 (기본값: 분)
     * @return 생성 후 경과 시간
     */
    fun age(unit: ChronoUnit = ChronoUnit.MINUTES): Long =
        unit.between(createdAt, LocalDateTime.now())

    /**
     * 마지막 수정 후 경과 시간 계산
     *
     * @param unit 시간 단위 (기본값: 분)
     * @return 마지막 수정 후 경과 시간, 수정 이력이 없으면 null
     */
    fun timeSinceLastUpdate(unit: ChronoUnit = ChronoUnit.MINUTES): Long? =
        updatedAt?.let { unit.between(it, LocalDateTime.now()) }

    /**
     * 엔티티가 최근에 생성되었는지 확인
     *
     * @param thresholdMinutes 기준 시간 (분, 기본값: 10분)
     * @return 기준 시간 내에 생성되었으면 true
     */
    fun isRecentlyCreated(thresholdMinutes: Long = 10): Boolean =
        age(ChronoUnit.MINUTES) <= thresholdMinutes

    /**
     * 엔티티가 최근에 수정되었는지 확인
     *
     * @param thresholdMinutes 기준 시간 (분, 기본값: 10분)
     * @return 기준 시간 내에 수정되었으면 true, 수정 이력이 없으면 false
     */
    fun isRecentlyUpdated(thresholdMinutes: Long = 10): Boolean =
        timeSinceLastUpdate(ChronoUnit.MINUTES)?.let { it <= thresholdMinutes } ?: false
}

/**
 * BaseEntity 인터페이스 확장 함수들
 *
 * 도메인 엔티티의 공통 동작을 제공하는 유틸리티 함수들
 */

/**
 * 엔티티의 생명주기 상태를 문자열로 반환
 */
fun BaseEntity.lifecycleStatus(): String = when {
    isNew() -> "NEW"
    isRecentlyUpdated() -> "RECENTLY_UPDATED"
    isRecentlyCreated() -> "RECENTLY_CREATED"
    else -> "STABLE"
}

/**
 * 엔티티의 기본 정보를 문자열로 반환
 */
fun BaseEntity.toSummary(): String =
    "Entity[id=$id, created=$createdAt, updated=$updatedAt, status=${lifecycleStatus()}]"

/**
 * 두 BaseEntity가 같은 엔티티인지 ID로 비교
 */
infix fun BaseEntity.isSameEntityAs(other: BaseEntity): Boolean = this.id == other.id

/**
 * 엔티티가 다른 엔티티보다 먼저 생성되었는지 확인
 */
infix fun BaseEntity.wasCreatedBefore(other: BaseEntity): Boolean = this.createdAt.isBefore(other.createdAt)

/**
 * 엔티티가 다른 엔티티보다 나중에 생성되었는지 확인
 */
infix fun BaseEntity.wasCreatedAfter(other: BaseEntity): Boolean = this.createdAt.isAfter(other.createdAt)