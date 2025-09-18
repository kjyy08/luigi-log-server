package cloud.luigi99.blog.common.exception

/**
 * 도메인 계층에서 발생하는 예외를 나타내는 클래스
 *
 * 개인 기술 블로그 프로젝트에서 도메인 규칙 위반이나 비즈니스 불변 조건 위반 시 발생하는
 * 예외들을 처리하기 위한 클래스입니다.
 *
 * 도메인 예외는 다음 특징을 가집니다:
 * - 도메인 규칙 위반: 비즈니스 불변 조건이나 도메인 규칙 위반
 * - 명확한 도메인 컨텍스트: 어떤 도메인에서 발생했는지 명시
 * - 복구 가능: 사용자 행동 변경으로 해결 가능한 문제
 */
class DomainException : BusinessException {

    /**
     * 도메인 컨텍스트
     * 예외가 발생한 도메인 영역을 나타냄 (예: "User", "Post", "Comment")
     */
    val domainContext: String

    /**
     * 기본 생성자
     *
     * @param message 에러 메시지
     * @param cause 원인 예외 (선택사항)
     */
    constructor(
        message: String,
        cause: Throwable? = null
    ) : super("DOMAIN_ERROR", message, cause) {
        this.domainContext = "Unknown"
    }

    /**
     * 도메인 컨텍스트를 포함한 생성자
     *
     * @param domainContext 도메인 컨텍스트
     * @param message 에러 메시지
     * @param cause 원인 예외 (선택사항)
     */
    constructor(
        domainContext: String,
        message: String,
        cause: Throwable? = null
    ) : super("DOMAIN_ERROR", message, cause) {
        this.domainContext = domainContext
    }

    /**
     * 도메인별 에러 코드를 포함한 생성자
     *
     * @param domainContext 도메인 컨텍스트
     * @param errorCode 도메인별 에러 코드
     * @param message 에러 메시지
     * @param cause 원인 예외 (선택사항)
     */
    constructor(
        domainContext: String,
        errorCode: String,
        message: String,
        cause: Throwable? = null
    ) : super(errorCode, message, cause) {
        this.domainContext = domainContext
    }

    /**
     * 포맷된 메시지와 도메인 컨텍스트를 사용하는 생성자
     *
     * @param domainContext 도메인 컨텍스트
     * @param errorCode 에러 코드
     * @param messageFormat 메시지 포맷 (String.format 사용)
     * @param args 메시지 포맷 인자들
     */
    constructor(
        domainContext: String,
        errorCode: String,
        messageFormat: String,
        vararg args: Any
    ) : super(errorCode, messageFormat, *args) {
        this.domainContext = domainContext
    }

    /**
     * 다른 DomainException을 기반으로 새로운 예외를 생성하는 생성자
     *
     * @param other 기반이 되는 DomainException
     * @param additionalMessage 추가할 메시지
     */
    constructor(
        other: DomainException,
        additionalMessage: String? = null
    ) : super(other, additionalMessage) {
        this.domainContext = other.domainContext
    }

    /**
     * 예외가 특정 도메인 컨텍스트에서 발생했는지 확인합니다.
     *
     * @param context 확인할 도메인 컨텍스트
     * @return 도메인 컨텍스트가 일치하면 true, 다르면 false
     */
    fun isFromDomain(context: String): Boolean {
        return domainContext.equals(context, ignoreCase = true)
    }

    /**
     * 예외가 여러 도메인 컨텍스트 중 하나에서 발생했는지 확인합니다.
     *
     * @param contexts 확인할 도메인 컨텍스트들
     * @return 도메인 컨텍스트가 하나라도 일치하면 true, 모두 다르면 false
     */
    fun isFromAnyDomain(vararg contexts: String): Boolean {
        return contexts.any { domainContext.equals(it, ignoreCase = true) }
    }

    /**
     * 도메인 컨텍스트를 포함한 상세한 에러 정보를 문자열로 반환합니다.
     *
     * @return 도메인 컨텍스트, 에러 코드, 메시지를 포함한 문자열
     */
    override fun getDetailedMessage(): String {
        return "[$domainContext:$errorCode] $message"
    }

    /**
     * 예외의 문자열 표현을 반환합니다.
     *
     * @return 클래스명, 도메인 컨텍스트, 에러 코드, 메시지를 포함한 문자열
     */
    override fun toString(): String {
        return "${this::class.simpleName}(domainContext='$domainContext', errorCode='$errorCode', message='$message')"
    }

    companion object {
        /**
         * 엔티티를 찾을 수 없을 때 발생하는 예외를 생성합니다.
         *
         * @param domainContext 도메인 컨텍스트
         * @param entityId 찾을 수 없는 엔티티의 ID
         * @return DomainException 인스턴스
         */
        fun entityNotFound(domainContext: String, entityId: Any): DomainException {
            return DomainException(
                domainContext,
                "ENTITY_NOT_FOUND",
                "%s를 찾을 수 없습니다. ID: %s",
                domainContext,
                entityId
            )
        }

        /**
         * 비즈니스 규칙 위반 시 발생하는 예외를 생성합니다.
         *
         * @param domainContext 도메인 컨텍스트
         * @param ruleName 위반된 규칙명
         * @param reason 위반 사유
         * @return DomainException 인스턴스
         */
        fun businessRuleViolation(domainContext: String, ruleName: String, reason: String): DomainException {
            return DomainException(
                domainContext,
                "BUSINESS_RULE_VIOLATION",
                "%s 도메인의 '%s' 규칙이 위반되었습니다: %s",
                domainContext,
                ruleName,
                reason
            )
        }

        /**
         * 유효하지 않은 상태 전환 시 발생하는 예외를 생성합니다.
         *
         * @param domainContext 도메인 컨텍스트
         * @param currentState 현재 상태
         * @param targetState 목표 상태
         * @return DomainException 인스턴스
         */
        fun invalidStateTransition(domainContext: String, currentState: String, targetState: String): DomainException {
            return DomainException(
                domainContext,
                "INVALID_STATE_TRANSITION",
                "%s 상태에서 %s 상태로 전환할 수 없습니다",
                currentState,
                targetState
            )
        }
    }
}