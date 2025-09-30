package cloud.luigi99.blog.common.domain

import java.time.LocalDateTime
import java.util.*

/**
 * 모든 엔티티의 기반이 되는 추상 클래스
 *
 * 개인 기술 블로그 프로젝트에서 사용되는 모든 도메인 엔티티는 이 클래스를 상속받아
 * 공통적인 식별자와 감사 정보를 제공받습니다.
 *
 * @param T 엔티티 식별자 타입 (ValueObject를 상속받은 타입)
 */
abstract class BaseEntity<out T : ValueObject> {
    /**
     * 엔티티의 고유 식별자
     * ValueObject 패턴을 따르는 타입 안전한 식별자를 사용합니다.
     */
    abstract val entityId: T

    /**
     * 엔티티 생성 시각
     * 엔티티가 처음 생성된 시점을 기록합니다.
     */
    abstract val createdAt: LocalDateTime

    /**
     * 엔티티 최종 수정 시각
     * 엔티티가 마지막으로 수정된 시점을 기록합니다.
     * 생성 시에는 null이며, 수정이 발생할 때 값이 설정됩니다.
     */
    abstract var updatedAt: LocalDateTime?

    /**
     * EntityId를 기준으로 엔티티의 동등성을 판단합니다.
     *
     * @param other 비교할 객체
     * @return EntityId가 같으면 true, 다르면 false
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseEntity<*>) return false
        return entityId == other.entityId
    }

    /**
     * EntityId를 기준으로 해시코드를 생성합니다.
     *
     * @return EntityId의 해시코드
     */
    override fun hashCode(): Int {
        return entityId.hashCode()
    }

    /**
     * 엔티티의 문자열 표현을 생성합니다.
     *
     * @return 클래스명과 EntityId를 포함한 문자열
     */
    override fun toString(): String {
        return "${this::class.simpleName}(entityId=$entityId, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}