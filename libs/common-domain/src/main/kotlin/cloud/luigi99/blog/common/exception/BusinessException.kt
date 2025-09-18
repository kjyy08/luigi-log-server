package cloud.luigi99.blog.common.exception

/**
 * 비즈니스 로직 예외의 기반이 되는 클래스
 *
 * 개인 기술 블로그 프로젝트에서 발생하는 모든 비즈니스 로직 관련 예외는 이 클래스를 상속받아
 * 일관된 에러 처리와 메시지 관리를 제공받습니다.
 *
 * 비즈니스 예외는 다음 특징을 가집니다:
 * - 복구 가능한 예외: 사용자 입력 수정으로 해결 가능
 * - 명확한 에러 코드: 클라이언트에서 에러 처리 용이
 * - 사용자 친화적 메시지: 최종 사용자가 이해할 수 있는 메시지
 */
open class BusinessException : RuntimeException {

    /**
     * 에러 코드
     * 클라이언트에서 에러를 구분하고 처리하기 위한 코드
     */
    val errorCode: String

    /**
     * 기본 생성자
     *
     * @param message 에러 메시지
     * @param cause 원인 예외 (선택사항)
     */
    constructor(
        message: String,
        cause: Throwable? = null
    ) : super(message, cause) {
        this.errorCode = ErrorCode.COMMON_BUSINESS_ERROR.code
    }

    /**
     * 에러 코드를 포함한 생성자
     *
     * @param errorCode 에러 코드
     * @param message 에러 메시지
     * @param cause 원인 예외 (선택사항)
     */
    constructor(
        errorCode: String,
        message: String,
        cause: Throwable? = null
    ) : super(message, cause) {
        this.errorCode = errorCode
    }

    /**
     * 포맷된 메시지를 사용하는 생성자
     *
     * @param errorCode 에러 코드
     * @param messageFormat 메시지 포맷 (String.format 사용)
     * @param args 메시지 포맷 인자들
     */
    constructor(
        errorCode: String,
        messageFormat: String,
        vararg args: Any
    ) : super(messageFormat.format(*args)) {
        this.errorCode = errorCode
    }

    /**
     * 다른 BusinessException을 기반으로 새로운 예외를 생성하는 생성자
     *
     * @param other 기반이 되는 BusinessException
     * @param additionalMessage 추가할 메시지
     */
    constructor(
        other: BusinessException,
        additionalMessage: String? = null
    ) : super(
        if (additionalMessage != null) "${other.message} - $additionalMessage" else other.message,
        other.cause
    ) {
        this.errorCode = other.errorCode
    }

    /**
     * 예외가 특정 에러 코드를 가지는지 확인합니다.
     *
     * @param code 확인할 에러 코드
     * @return 에러 코드가 일치하면 true, 다르면 false
     */
    fun hasErrorCode(code: String): Boolean {
        return errorCode == code
    }

    /**
     * 예외가 여러 에러 코드 중 하나를 가지는지 확인합니다.
     *
     * @param codes 확인할 에러 코드들
     * @return 에러 코드가 하나라도 일치하면 true, 모두 다르면 false
     */
    fun hasAnyErrorCode(vararg codes: String): Boolean {
        return codes.contains(errorCode)
    }

    /**
     * 상세한 에러 정보를 문자열로 반환합니다.
     *
     * @return 에러 코드와 메시지를 포함한 문자열
     */
    open fun getDetailedMessage(): String {
        return "[$errorCode] $message"
    }

    /**
     * 예외의 문자열 표현을 반환합니다.
     *
     * @return 클래스명, 에러 코드, 메시지를 포함한 문자열
     */
    override fun toString(): String {
        return "${this::class.simpleName}(errorCode='$errorCode', message='$message')"
    }
}