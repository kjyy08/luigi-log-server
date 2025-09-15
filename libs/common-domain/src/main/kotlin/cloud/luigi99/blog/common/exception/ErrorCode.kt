package cloud.luigi99.blog.common.exception

/**
 * 시스템 전반에서 사용되는 오류 코드 정의
 * 각 코드는 고유하며 클라이언트 측에서 오류 타입 식별에 사용됩니다.
 */
enum class ErrorCode(
    val code: String,
    val defaultMessage: String,
    val category: ErrorCategory
) {
    // 일반적인 시스템 오류 (1000번대)
    INTERNAL_SERVER_ERROR("SYSTEM_1001", "내부 서버 오류가 발생했습니다.", ErrorCategory.SYSTEM),
    INVALID_INPUT("SYSTEM_1002", "잘못된 입력입니다.", ErrorCategory.VALIDATION),
    UNAUTHORIZED("SYSTEM_1003", "인증이 필요합니다.", ErrorCategory.SECURITY),
    ACCESS_DENIED("SYSTEM_1004", "접근이 거부되었습니다.", ErrorCategory.SECURITY),
    RESOURCE_NOT_FOUND("SYSTEM_1005", "요청한 리소스를 찾을 수 없습니다.", ErrorCategory.RESOURCE),

    // 도메인 관련 오류 (2000번대)
    DOMAIN_RULE_VIOLATION("DOMAIN_2001", "도메인 규칙 위반입니다.", ErrorCategory.BUSINESS),
    ENTITY_NOT_FOUND("DOMAIN_2002", "엔티티를 찾을 수 없습니다.", ErrorCategory.RESOURCE),
    INVALID_OPERATION("DOMAIN_2003", "잘못된 연산 시도입니다.", ErrorCategory.BUSINESS),
    CONCURRENT_MODIFICATION("DOMAIN_2004", "동시 수정 충돌이 발생했습니다.", ErrorCategory.CONCURRENCY),
    AGGREGATE_CONSISTENCY_VIOLATION("DOMAIN_2005", "애그리게이트 일관성 위반입니다.", ErrorCategory.BUSINESS),

    // 사용자 관련 오류 (3000번대)
    USER_NOT_FOUND("USER_3001", "사용자를 찾을 수 없습니다.", ErrorCategory.RESOURCE),
    USER_ALREADY_EXISTS("USER_3002", "이미 존재하는 사용자입니다.", ErrorCategory.BUSINESS),
    INVALID_CREDENTIALS("USER_3003", "잘못된 인증 정보입니다.", ErrorCategory.SECURITY),
    USER_INACTIVE("USER_3004", "비활성화된 사용자입니다.", ErrorCategory.BUSINESS),

    // 콘텐츠 관련 오류 (4000번대)
    CONTENT_NOT_FOUND("CONTENT_4001", "콘텐츠를 찾을 수 없습니다.", ErrorCategory.RESOURCE),
    CONTENT_ALREADY_PUBLISHED("CONTENT_4002", "이미 발행된 콘텐츠입니다.", ErrorCategory.BUSINESS),
    INVALID_CONTENT_STATUS("CONTENT_4003", "잘못된 콘텐츠 상태입니다.", ErrorCategory.BUSINESS),
    CONTENT_ACCESS_DENIED("CONTENT_4004", "콘텐츠 접근이 거부되었습니다.", ErrorCategory.SECURITY),

    // 검색 관련 오류 (5000번대)
    SEARCH_INDEX_ERROR("SEARCH_5001", "검색 인덱스 오류입니다.", ErrorCategory.SYSTEM),
    INVALID_SEARCH_QUERY("SEARCH_5002", "잘못된 검색 쿼리입니다.", ErrorCategory.VALIDATION),
    SEARCH_SERVICE_UNAVAILABLE("SEARCH_5003", "검색 서비스를 사용할 수 없습니다.", ErrorCategory.SYSTEM);

    /**
     * 오류 코드의 전체 식별자를 반환합니다.
     */
    fun getFullCode(): String = code

    /**
     * 카테고리와 함께 포맷된 메시지를 반환합니다.
     */
    fun getFormattedMessage(): String = "[$category] $defaultMessage"
}

/**
 * 오류 카테고리 분류
 */
enum class ErrorCategory(val displayName: String) {
    SYSTEM("시스템 오류"),
    VALIDATION("검증 오류"),
    SECURITY("보안 오류"),
    RESOURCE("리소스 오류"),
    BUSINESS("비즈니스 로직 오류"),
    CONCURRENCY("동시성 오류")
}

/**
 * 로깅 레벨 정의
 */
enum class LogLevel {
    ERROR,    // 심각한 오류, 즉시 대응 필요
    WARN,     // 경고 수준, 모니터링 필요
    INFO,     // 정보성, 비즈니스 로직 추적용
    DEBUG     // 디버깅용, 개발 환경에서만
}

/**
 * 민감한 정보 마스킹을 위한 필드 타입
 */
enum class SensitiveFieldType {
    PASSWORD,
    EMAIL,
    PHONE,
    ID_NUMBER,
    CREDIT_CARD,
    CUSTOM
}