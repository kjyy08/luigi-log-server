package cloud.luigi99.blog.common.exception

import java.time.LocalDateTime
import java.util.UUID

/**
 * 예외 처리 관련 유틸리티 클래스
 *
 * 예외 생성, 변환, 분석을 위한 공통 기능을 제공합니다.
 */
object ExceptionUtils {

    /**
     * BusinessException 빌더
     */
    class BusinessExceptionBuilder {
        private var errorCode: ErrorCode = ErrorCode.INTERNAL_SERVER_ERROR
        private var messageKey: String? = null
        private var cause: Throwable? = null
        private var logLevel: LogLevel = LogLevel.ERROR
        private var correlationId: String? = null
        private var userId: String? = null
        private val context: MutableMap<String, Any?> = mutableMapOf()

        fun errorCode(errorCode: ErrorCode) = apply { this.errorCode = errorCode }
        fun messageKey(messageKey: String?) = apply { this.messageKey = messageKey }
        fun cause(cause: Throwable?) = apply { this.cause = cause }
        fun logLevel(logLevel: LogLevel) = apply { this.logLevel = logLevel }
        fun correlationId(correlationId: String?) = apply { this.correlationId = correlationId }
        fun userId(userId: String?) = apply { this.userId = userId }
        fun context(key: String, value: Any?) = apply { this.context[key] = value }
        fun context(contextMap: Map<String, Any?>) = apply { this.context.putAll(contextMap) }

        fun build(): BusinessException {
            return BusinessException(
                errorCode = errorCode,
                messageKey = messageKey,
                cause = cause,
                logLevel = logLevel,
                correlationId = correlationId,
                userId = userId
            ).apply {
                addContext(this@BusinessExceptionBuilder.context)
            }
        }
    }

    /**
     * DomainException 빌더
     */
    class DomainExceptionBuilder {
        private var errorCode: ErrorCode = ErrorCode.DOMAIN_RULE_VIOLATION
        private var domainType: String? = null
        private var aggregateId: String? = null
        private var aggregateVersion: Long? = null
        private var domainRule: String? = null
        private var operationName: String? = null
        private var currentState: String? = null
        private var expectedState: String? = null
        private var messageKey: String? = null
        private var cause: Throwable? = null
        private var logLevel: LogLevel = LogLevel.ERROR
        private var correlationId: String? = null
        private var userId: String? = null
        private val context: MutableMap<String, Any?> = mutableMapOf()

        fun errorCode(errorCode: ErrorCode) = apply { this.errorCode = errorCode }
        fun domainType(domainType: String?) = apply { this.domainType = domainType }
        fun aggregateId(aggregateId: String?) = apply { this.aggregateId = aggregateId }
        fun aggregateVersion(version: Long?) = apply { this.aggregateVersion = version }
        fun domainRule(rule: String?) = apply { this.domainRule = rule }
        fun operationName(operation: String?) = apply { this.operationName = operation }
        fun currentState(state: String?) = apply { this.currentState = state }
        fun expectedState(state: String?) = apply { this.expectedState = state }
        fun messageKey(messageKey: String?) = apply { this.messageKey = messageKey }
        fun cause(cause: Throwable?) = apply { this.cause = cause }
        fun logLevel(logLevel: LogLevel) = apply { this.logLevel = logLevel }
        fun correlationId(correlationId: String?) = apply { this.correlationId = correlationId }
        fun userId(userId: String?) = apply { this.userId = userId }
        fun context(key: String, value: Any?) = apply { this.context[key] = value }
        fun context(contextMap: Map<String, Any?>) = apply { this.context.putAll(contextMap) }

        fun build(): DomainException {
            return DomainException(
                errorCode = errorCode,
                domainType = domainType,
                aggregateId = aggregateId,
                aggregateVersion = aggregateVersion,
                domainRule = domainRule,
                operationName = operationName,
                currentState = currentState,
                expectedState = expectedState,
                messageKey = messageKey,
                cause = cause,
                logLevel = logLevel,
                correlationId = correlationId,
                userId = userId
            ).apply {
                addContext(this@DomainExceptionBuilder.context)
            }
        }
    }

    /**
     * BusinessException 빌더 생성
     */
    fun businessException(): BusinessExceptionBuilder = BusinessExceptionBuilder()

    /**
     * DomainException 빌더 생성
     */
    fun domainException(): DomainExceptionBuilder = DomainExceptionBuilder()

    /**
     * 상관관계 ID 생성
     */
    fun generateCorrelationId(): String = UUID.randomUUID().toString()

    /**
     * 예외 체인 분석
     */
    fun analyzeExceptionChain(throwable: Throwable): ExceptionChainAnalysis {
        val chain = mutableListOf<Throwable>()
        var current: Throwable? = throwable

        while (current != null) {
            chain.add(current)
            current = current.cause
        }

        val rootCause = chain.lastOrNull()
        val businessExceptions = chain.filterIsInstance<BusinessException>()
        val domainExceptions = chain.filterIsInstance<DomainException>()

        return ExceptionChainAnalysis(
            totalDepth = chain.size,
            rootCause = rootCause,
            businessExceptions = businessExceptions,
            domainExceptions = domainExceptions,
            hasSystemException = chain.any { it !is BusinessException },
            mostSpecificException = chain.firstOrNull()
        )
    }

    /**
     * 예외를 클라이언트 안전 형태로 변환
     */
    fun toClientSafeException(throwable: Throwable, correlationId: String? = null): Map<String, Any?> {
        return when (throwable) {
            is BusinessException -> throwable.getClientSafeError()
            is IllegalArgumentException -> mapOf(
                "errorCode" to ErrorCode.INVALID_INPUT.code,
                "message" to "잘못된 요청입니다",
                "category" to ErrorCategory.VALIDATION.displayName,
                "timestamp" to LocalDateTime.now(),
                "correlationId" to correlationId
            )
            is IllegalStateException -> mapOf(
                "errorCode" to ErrorCode.INVALID_OPERATION.code,
                "message" to "현재 상태에서 수행할 수 없는 작업입니다",
                "category" to ErrorCategory.BUSINESS.displayName,
                "timestamp" to LocalDateTime.now(),
                "correlationId" to correlationId
            )
            else -> mapOf(
                "errorCode" to ErrorCode.INTERNAL_SERVER_ERROR.code,
                "message" to "시스템 오류가 발생했습니다",
                "category" to ErrorCategory.SYSTEM.displayName,
                "timestamp" to LocalDateTime.now(),
                "correlationId" to correlationId
            )
        }
    }

    /**
     * 예외 심각도 평가
     */
    fun evaluateSeverity(throwable: Throwable): ExceptionSeverity {
        return when (throwable) {
            is BusinessException -> when (throwable.logLevel) {
                LogLevel.ERROR -> ExceptionSeverity.HIGH
                LogLevel.WARN -> ExceptionSeverity.MEDIUM
                LogLevel.INFO -> ExceptionSeverity.LOW
                LogLevel.DEBUG -> ExceptionSeverity.NEGLIGIBLE
            }
            is SecurityException -> ExceptionSeverity.CRITICAL
            is OutOfMemoryError -> ExceptionSeverity.CRITICAL
            is StackOverflowError -> ExceptionSeverity.CRITICAL
            is IllegalArgumentException -> ExceptionSeverity.LOW
            is IllegalStateException -> ExceptionSeverity.MEDIUM
            else -> ExceptionSeverity.HIGH
        }
    }

    /**
     * 예외 스택 트레이스 최적화
     */
    fun optimizeStackTrace(throwable: Throwable, maxFrames: Int = 10): List<String> {
        return throwable.stackTrace
            .filterNot { element ->
                // 프레임워크 스택 제거
                element.className.startsWith("org.springframework") ||
                element.className.startsWith("java.lang.reflect") ||
                element.className.startsWith("org.apache.catalina") ||
                element.className.startsWith("org.eclipse.jetty") ||
                element.className.startsWith("com.sun.proxy")
            }
            .take(maxFrames)
            .map { "${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})" }
    }

    /**
     * 민감한 정보 마스킹
     */
    fun maskSensitiveData(data: Any?, fieldType: SensitiveFieldType): String? {
        if (data == null) return null

        val dataStr = data.toString()
        return when (fieldType) {
            SensitiveFieldType.PASSWORD -> "***MASKED***"
            SensitiveFieldType.EMAIL -> maskEmail(dataStr)
            SensitiveFieldType.PHONE -> maskPhone(dataStr)
            SensitiveFieldType.ID_NUMBER -> maskIdNumber(dataStr)
            SensitiveFieldType.CREDIT_CARD -> maskCreditCard(dataStr)
            SensitiveFieldType.CUSTOM -> "***${dataStr.take(2)}***"
        }
    }

    /**
     * 예외 발생 패턴 분석
     */
    fun analyzeExceptionPattern(
        exceptions: List<BusinessException>,
        timeWindowMinutes: Long = 60
    ): ExceptionPattern {
        val now = LocalDateTime.now()
        val recentExceptions = exceptions.filter {
            it.timestamp.isAfter(now.minusMinutes(timeWindowMinutes))
        }

        val byErrorCode = recentExceptions.groupBy { it.errorCode }
        val byDomain = recentExceptions.filterIsInstance<DomainException>()
            .groupBy { it.domainType }
        val byUser = recentExceptions.filter { it.userId != null }
            .groupBy { it.userId }

        return ExceptionPattern(
            totalCount = recentExceptions.size,
            timeWindow = timeWindowMinutes,
            mostFrequentErrorCode = byErrorCode.maxByOrNull { it.value.size }?.key,
            mostAffectedDomain = byDomain.maxByOrNull { it.value.size }?.key,
            mostAffectedUser = byUser.maxByOrNull { it.value.size }?.key,
            errorCodeDistribution = byErrorCode.mapValues { it.value.size },
            domainDistribution = byDomain.mapValues { it.value.size }
        )
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

/**
 * 예외 체인 분석 결과
 */
data class ExceptionChainAnalysis(
    val totalDepth: Int,
    val rootCause: Throwable?,
    val businessExceptions: List<BusinessException>,
    val domainExceptions: List<DomainException>,
    val hasSystemException: Boolean,
    val mostSpecificException: Throwable?
)

/**
 * 예외 심각도
 */
enum class ExceptionSeverity(val level: Int, val description: String) {
    CRITICAL(5, "즉시 대응 필요"),
    HIGH(4, "높은 우선순위"),
    MEDIUM(3, "중간 우선순위"),
    LOW(2, "낮은 우선순위"),
    NEGLIGIBLE(1, "무시 가능")
}

/**
 * 예외 발생 패턴 분석 결과
 */
data class ExceptionPattern(
    val totalCount: Int,
    val timeWindow: Long,
    val mostFrequentErrorCode: ErrorCode?,
    val mostAffectedDomain: String?,
    val mostAffectedUser: String?,
    val errorCodeDistribution: Map<ErrorCode, Int>,
    val domainDistribution: Map<String?, Int>
)