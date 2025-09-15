package cloud.luigi99.blog.common.exception

import java.time.LocalDateTime

/**
 * 비즈니스 로직 관련 예외의 기본 클래스
 *
 * 이 클래스는 모든 비즈니스 예외의 공통 기능을 제공합니다:
 * - 표준화된 오류 코드 시스템
 * - 컨텍스트 정보 수집 및 추적
 * - 다국어 지원을 위한 메시지 키
 * - 로깅 레벨 제어
 * - 민감한 정보 마스킹
 */
open class BusinessException : RuntimeException {

    val errorCode: ErrorCode
    val messageKey: String?
    val context: MutableMap<String, Any?>
    val logLevel: LogLevel
    val timestamp: LocalDateTime
    val correlationId: String?
    val userId: String?

    /**
     * 기본 생성자 - ErrorCode 기반
     */
    constructor(
        errorCode: ErrorCode,
        messageKey: String? = null,
        cause: Throwable? = null,
        logLevel: LogLevel = LogLevel.ERROR,
        correlationId: String? = null,
        userId: String? = null
    ) : super(messageKey ?: errorCode.defaultMessage, cause) {
        this.errorCode = errorCode
        this.messageKey = messageKey
        this.context = mutableMapOf()
        this.logLevel = logLevel
        this.timestamp = LocalDateTime.now()
        this.correlationId = correlationId
        this.userId = userId
    }

    /**
     * 메시지 기반 생성자 (하위 호환성)
     */
    constructor(
        message: String,
        cause: Throwable? = null,
        errorCode: ErrorCode = ErrorCode.INTERNAL_SERVER_ERROR,
        logLevel: LogLevel = LogLevel.ERROR
    ) : super(message, cause) {
        this.errorCode = errorCode
        this.messageKey = null
        this.context = mutableMapOf()
        this.logLevel = logLevel
        this.timestamp = LocalDateTime.now()
        this.correlationId = null
        this.userId = null
    }

    /**
     * 컨텍스트 정보 추가
     */
    fun addContext(key: String, value: Any?): BusinessException {
        context[key] = value
        return this
    }

    /**
     * 여러 컨텍스트 정보 추가
     */
    fun addContext(contextMap: Map<String, Any?>): BusinessException {
        context.putAll(contextMap)
        return this
    }

    /**
     * 민감한 정보가 포함된 컨텍스트 추가 (자동 마스킹)
     */
    fun addSensitiveContext(key: String, value: Any?, fieldType: SensitiveFieldType): BusinessException {
        context[key] = maskSensitiveData(value, fieldType)
        return this
    }

    /**
     * 사용자 ID 설정
     */
    fun withUserId(userId: String): BusinessException {
        return BusinessException(
            errorCode = this.errorCode,
            messageKey = this.messageKey,
            cause = this.cause,
            logLevel = this.logLevel,
            correlationId = this.correlationId,
            userId = userId
        ).also {
            it.context.putAll(this.context)
        }
    }

    /**
     * 상관관계 ID 설정
     */
    fun withCorrelationId(correlationId: String): BusinessException {
        return BusinessException(
            errorCode = this.errorCode,
            messageKey = this.messageKey,
            cause = this.cause,
            logLevel = this.logLevel,
            correlationId = correlationId,
            userId = this.userId
        ).also {
            it.context.putAll(this.context)
        }
    }

    /**
     * 오류 발생 시점의 스냅샷 정보 반환
     */
    open fun getErrorSnapshot(): Map<String, Any?> {
        return mapOf(
            "errorCode" to errorCode.code,
            "message" to message,
            "messageKey" to messageKey,
            "category" to errorCode.category,
            "logLevel" to logLevel,
            "timestamp" to timestamp,
            "correlationId" to correlationId,
            "userId" to userId,
            "context" to context.toMap(),
            "stackTrace" to getOptimizedStackTrace()
        )
    }

    /**
     * 클라이언트 응답용 안전한 오류 정보 반환
     */
    open fun getClientSafeError(): Map<String, Any?> {
        return mapOf(
            "errorCode" to errorCode.code,
            "message" to (messageKey ?: errorCode.defaultMessage),
            "category" to errorCode.category.displayName,
            "timestamp" to timestamp,
            "correlationId" to correlationId,
            "context" to getClientSafeContext()
        )
    }

    /**
     * 민감한 정보 마스킹
     */
    private fun maskSensitiveData(value: Any?, fieldType: SensitiveFieldType): String? {
        if (value == null) return null

        val valueStr = value.toString()
        return when (fieldType) {
            SensitiveFieldType.PASSWORD -> "***MASKED***"
            SensitiveFieldType.EMAIL -> maskEmail(valueStr)
            SensitiveFieldType.PHONE -> maskPhone(valueStr)
            SensitiveFieldType.ID_NUMBER -> maskIdNumber(valueStr)
            SensitiveFieldType.CREDIT_CARD -> maskCreditCard(valueStr)
            SensitiveFieldType.CUSTOM -> "***${valueStr.take(2)}***"
        }
    }

    /**
     * 클라이언트에게 안전한 컨텍스트 정보만 반환
     */
    private fun getClientSafeContext(): Map<String, Any?> {
        return context.filterKeys { key ->
            // 민감한 정보가 포함된 키는 제외
            !key.lowercase().contains("password") &&
            !key.lowercase().contains("secret") &&
            !key.lowercase().contains("token") &&
            !key.lowercase().contains("key")
        }
    }

    /**
     * 최적화된 스택 트레이스 반환 (불필요한 프레임워크 스택 제거)
     */
    private fun getOptimizedStackTrace(): List<String> {
        return stackTrace
            .filterNot { element ->
                element.className.startsWith("org.springframework") ||
                element.className.startsWith("java.lang.reflect") ||
                element.className.startsWith("org.apache.catalina")
            }
            .take(10) // 상위 10개 스택만 포함
            .map { "${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})" }
    }

    // 마스킹 유틸리티 메서드들
    private fun maskEmail(email: String): String {
        val atIndex = email.indexOf('@')
        if (atIndex <= 0) return "***@***.***"
        val prefix = email.substring(0, minOf(2, atIndex))
        val domain = email.substring(atIndex)
        return "$prefix***$domain"
    }

    private fun maskPhone(phone: String): String {
        if (phone.length < 4) return "***"
        return phone.take(3) + "****" + phone.takeLast(2)
    }

    private fun maskIdNumber(idNumber: String): String {
        if (idNumber.length < 6) return "***"
        return idNumber.take(6) + "***"
    }

    private fun maskCreditCard(cardNumber: String): String {
        if (cardNumber.length < 4) return "****"
        return "****-****-****-${cardNumber.takeLast(4)}"
    }
}