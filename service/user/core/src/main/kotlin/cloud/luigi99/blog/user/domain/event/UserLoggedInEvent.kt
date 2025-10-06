package cloud.luigi99.blog.user.domain.event

import cloud.luigi99.blog.common.domain.BaseDomainEvent
import cloud.luigi99.blog.user.domain.vo.Email
import cloud.luigi99.blog.user.domain.vo.UserId
import java.time.LocalDateTime

/**
 * 사용자 로그인 도메인 이벤트
 *
 * 사용자가 로그인했을 때 발행되는 도메인 이벤트입니다.
 * 보안 모니터링, 사용자 활동 분석 등에 활용됩니다.
 *
 * 주요 사용 예시:
 * - 비정상적인 로그인 시도 감지
 * - 사용자 활동 로그 기록
 * - 분석 시스템에 로그인 이벤트 전송
 *
 * @property userId 로그인한 사용자 ID
 * @property email 사용자 이메일
 * @property loginAt 로그인 시각
 * @property ipAddress 로그인 IP 주소 (선택)
 */
data class UserLoggedInEvent(
    val userId: UserId,
    val email: Email,
    val loginAt: LocalDateTime,
    val ipAddress: String? = null
) : BaseDomainEvent(
    aggregateId = userId.value,
    eventType = "UserLoggedIn"
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
