package cloud.luigi99.blog.user.domain.event

import cloud.luigi99.blog.common.domain.BaseDomainEvent
import cloud.luigi99.blog.user.domain.vo.Email
import cloud.luigi99.blog.user.domain.vo.UserId
import java.time.LocalDateTime

/**
 * 사용자 프로필 업데이트 도메인 이벤트
 *
 * 사용자 프로필이 업데이트되었을 때 발행되는 도메인 이벤트입니다.
 * 검색 인덱스 업데이트, 캐시 무효화 등에 활용됩니다.
 *
 * 주요 사용 예시:
 * - Elasticsearch 사용자 정보 동기화
 * - 캐시 무효화 처리
 * - 사용자 활동 로그 기록
 *
 * @property userId 프로필을 업데이트한 사용자 ID
 * @property email 사용자 이메일
 * @property updatedAt 업데이트 시각
 */
data class UserProfileUpdatedEvent(
    val userId: UserId,
    val email: Email,
    val updatedAt: LocalDateTime
) : BaseDomainEvent(
    aggregateId = userId.value,
    eventType = "UserProfileUpdated"
) {
    /**
     * 이벤트 버전
     * 이벤트 스키마 버전을 나타냅니다.
     */
    override val eventVersion: Int = 1

    /**
     * 애그리게이트 타입
     * 이벤트를 발생시킨 애그리게이트의 타입을 나타냅니다.
     */
    override val aggregateType: String = "User"
}
