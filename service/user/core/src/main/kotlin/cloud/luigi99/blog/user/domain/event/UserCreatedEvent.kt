package cloud.luigi99.blog.user.domain.event

import cloud.luigi99.blog.common.domain.BaseDomainEvent
import cloud.luigi99.blog.user.domain.vo.Email
import cloud.luigi99.blog.user.domain.vo.UserId
import java.time.LocalDateTime

/**
 * 사용자 생성 도메인 이벤트
 *
 * 새로운 사용자가 생성되었을 때 발행되는 도메인 이벤트입니다.
 * 다른 바운디드 컨텍스트에서 사용자 생성을 감지하고 필요한 후속 처리를 수행할 수 있습니다.
 *
 * 주요 사용 예시:
 * - 환영 이메일 발송
 * - 분석 시스템에 사용자 가입 기록
 * - 추천 시스템에 신규 사용자 등록
 *
 * @property userId 생성된 사용자 ID
 * @property email 사용자 이메일
 * @property createdAt 사용자 생성 시각
 */
data class UserCreatedEvent(
    val userId: UserId,
    val email: Email,
    val createdAt: LocalDateTime
) : BaseDomainEvent(
    aggregateId = userId.value,
    eventType = "UserCreated"
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
