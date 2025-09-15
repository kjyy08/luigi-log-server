package cloud.luigi99.blog.common.exception

/**
 * 도메인 로직 관련 예외 클래스
 *
 * BusinessException을 확장하여 도메인 특화 기능을 제공합니다:
 * - 애그리게이트 식별자 추적
 * - 도메인 규칙 위반 정보
 * - 비즈니스 연산자 및 상태 추적
 * - 도메인 이벤트 관련 컨텍스트
 */
open class DomainException : BusinessException {

    val domainType: String?
    val aggregateId: String?
    val aggregateVersion: Long?
    val domainRule: String?
    val operationName: String?
    val currentState: String?
    val expectedState: String?

    /**
     * 도메인 특화 생성자
     */
    constructor(
        errorCode: ErrorCode,
        domainType: String? = null,
        aggregateId: String? = null,
        aggregateVersion: Long? = null,
        domainRule: String? = null,
        operationName: String? = null,
        currentState: String? = null,
        expectedState: String? = null,
        messageKey: String? = null,
        cause: Throwable? = null,
        logLevel: LogLevel = LogLevel.ERROR,
        correlationId: String? = null,
        userId: String? = null
    ) : super(
        errorCode = errorCode,
        messageKey = messageKey,
        cause = cause,
        logLevel = logLevel,
        correlationId = correlationId,
        userId = userId
    ) {
        this.domainType = domainType
        this.aggregateId = aggregateId
        this.aggregateVersion = aggregateVersion
        this.domainRule = domainRule
        this.operationName = operationName
        this.currentState = currentState
        this.expectedState = expectedState

        // 도메인 특화 컨텍스트 자동 추가
        initializeDomainContext()
    }

    /**
     * 하위 호환성을 위한 생성자
     */
    constructor(
        message: String,
        cause: Throwable? = null,
        errorCode: ErrorCode = ErrorCode.DOMAIN_RULE_VIOLATION
    ) : super(message, cause, errorCode) {
        this.domainType = null
        this.aggregateId = null
        this.aggregateVersion = null
        this.domainRule = null
        this.operationName = null
        this.currentState = null
        this.expectedState = null
    }

    /**
     * 애그리게이트 정보 설정
     */
    fun withAggregate(
        domainType: String,
        aggregateId: String,
        version: Long? = null
    ): DomainException {
        return DomainException(
            errorCode = this.errorCode,
            domainType = domainType,
            aggregateId = aggregateId,
            aggregateVersion = version,
            domainRule = this.domainRule,
            operationName = this.operationName,
            currentState = this.currentState,
            expectedState = this.expectedState,
            messageKey = this.messageKey,
            cause = this.cause,
            logLevel = this.logLevel,
            correlationId = this.correlationId,
            userId = this.userId
        ).also {
            it.context.putAll(this.context)
        }
    }

    /**
     * 도메인 규칙 정보 설정
     */
    fun withDomainRule(
        ruleName: String,
        operationName: String? = null
    ): DomainException {
        return DomainException(
            errorCode = this.errorCode,
            domainType = this.domainType,
            aggregateId = this.aggregateId,
            aggregateVersion = this.aggregateVersion,
            domainRule = ruleName,
            operationName = operationName ?: this.operationName,
            currentState = this.currentState,
            expectedState = this.expectedState,
            messageKey = this.messageKey,
            cause = this.cause,
            logLevel = this.logLevel,
            correlationId = this.correlationId,
            userId = this.userId
        ).also {
            it.context.putAll(this.context)
        }
    }

    /**
     * 상태 전환 실패 정보 설정
     */
    fun withStateTransition(
        currentState: String,
        expectedState: String,
        operationName: String? = null
    ): DomainException {
        return DomainException(
            errorCode = this.errorCode,
            domainType = this.domainType,
            aggregateId = this.aggregateId,
            aggregateVersion = this.aggregateVersion,
            domainRule = this.domainRule,
            operationName = operationName ?: this.operationName,
            currentState = currentState,
            expectedState = expectedState,
            messageKey = this.messageKey,
            cause = this.cause,
            logLevel = this.logLevel,
            correlationId = this.correlationId,
            userId = this.userId
        ).also {
            it.context.putAll(this.context)
        }
    }

    /**
     * 도메인 이벤트 처리 실패 정보 설정
     */
    fun withDomainEventFailure(
        eventType: String,
        eventId: String? = null,
        handlerName: String? = null
    ): DomainException {
        return this.addContext(mapOf(
            "eventType" to eventType,
            "eventId" to eventId,
            "handlerName" to handlerName,
            "failureType" to "DOMAIN_EVENT_PROCESSING"
        )) as DomainException
    }

    /**
     * 도메인 특화 오류 스냅샷 반환
     */
    override fun getErrorSnapshot(): Map<String, Any?> {
        val baseSnapshot = super.getErrorSnapshot().toMutableMap()

        baseSnapshot.putAll(mapOf(
            "domainType" to domainType,
            "aggregateId" to aggregateId,
            "aggregateVersion" to aggregateVersion,
            "domainRule" to domainRule,
            "operationName" to operationName,
            "currentState" to currentState,
            "expectedState" to expectedState,
            "violationType" to getViolationType()
        ))

        return baseSnapshot
    }

    /**
     * 도메인 특화 클라이언트 안전 오류 정보
     */
    override fun getClientSafeError(): Map<String, Any?> {
        val baseError = super.getClientSafeError().toMutableMap()

        baseError.putAll(mapOf(
            "domainType" to domainType,
            "operationName" to operationName,
            "violationType" to getViolationType(),
            // aggregateId는 민감할 수 있으므로 마스킹
            "resourceId" to aggregateId?.let { maskResourceId(it) }
        ))

        return baseError
    }

    /**
     * 도메인 컨텍스트 초기화
     */
    private fun initializeDomainContext() {
        domainType?.let { addContext("domainType", it) }
        aggregateId?.let { addContext("aggregateId", it) }
        aggregateVersion?.let { addContext("aggregateVersion", it) }
        domainRule?.let { addContext("violatedRule", it) }
        operationName?.let { addContext("operation", it) }

        if (currentState != null && expectedState != null) {
            addContext("stateTransition", mapOf(
                "from" to currentState,
                "to" to expectedState
            ))
        }
    }

    /**
     * 위반 타입 결정
     */
    private fun getViolationType(): String {
        return when {
            domainRule != null -> "BUSINESS_RULE_VIOLATION"
            currentState != null && expectedState != null -> "STATE_TRANSITION_VIOLATION"
            aggregateId != null && aggregateVersion != null -> "AGGREGATE_CONSISTENCY_VIOLATION"
            else -> "DOMAIN_LOGIC_VIOLATION"
        }
    }

    /**
     * 리소스 ID 마스킹 (보안)
     */
    private fun maskResourceId(resourceId: String): String {
        if (resourceId.length <= 4) return "***"
        return resourceId.take(4) + "***"
    }

    companion object {
        /**
         * 엔티티를 찾을 수 없는 경우의 도메인 예외 생성
         */
        fun entityNotFound(
            domainType: String,
            aggregateId: String,
            operationName: String? = null
        ): DomainException {
            return DomainException(
                errorCode = ErrorCode.ENTITY_NOT_FOUND,
                domainType = domainType,
                aggregateId = aggregateId,
                operationName = operationName
            )
        }

        /**
         * 잘못된 연산 시도의 도메인 예외 생성
         */
        fun invalidOperation(
            domainType: String,
            operationName: String,
            currentState: String,
            aggregateId: String? = null,
            reason: String? = null
        ): DomainException {
            return DomainException(
                errorCode = ErrorCode.INVALID_OPERATION,
                domainType = domainType,
                aggregateId = aggregateId,
                operationName = operationName,
                currentState = currentState
            ).apply {
                reason?.let { addContext("reason", it) }
            }
        }

        /**
         * 도메인 규칙 위반의 도메인 예외 생성
         */
        fun ruleViolation(
            ruleName: String,
            domainType: String,
            aggregateId: String? = null,
            operationName: String? = null,
            details: String? = null
        ): DomainException {
            return DomainException(
                errorCode = ErrorCode.DOMAIN_RULE_VIOLATION,
                domainType = domainType,
                aggregateId = aggregateId,
                domainRule = ruleName,
                operationName = operationName
            ).apply {
                details?.let { addContext("ruleDetails", it) }
            }
        }

        /**
         * 동시 수정 충돌의 도메인 예외 생성
         */
        fun concurrentModification(
            domainType: String,
            aggregateId: String,
            expectedVersion: Long,
            actualVersion: Long
        ): DomainException {
            return DomainException(
                errorCode = ErrorCode.CONCURRENT_MODIFICATION,
                domainType = domainType,
                aggregateId = aggregateId,
                aggregateVersion = actualVersion
            ).addContext(mapOf(
                "expectedVersion" to expectedVersion,
                "actualVersion" to actualVersion,
                "conflictType" to "VERSION_MISMATCH"
            )) as DomainException
        }
    }
}