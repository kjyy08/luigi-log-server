package cloud.luigi99.blog.common.exception

/**
 * 도메인 규칙 위반 예외
 *
 * 비즈니스 규칙이나 불변식(invariant) 위반 시 발생합니다.
 * 도메인 모델의 일관성을 보장하는 핵심적인 예외입니다.
 */
class DomainRuleViolationException : DomainException {

    /**
     * 기본 규칙 위반 생성자
     */
    constructor(
        ruleName: String,
        domainType: String,
        ruleDescription: String? = null,
        aggregateId: String? = null,
        operationName: String? = null,
        violatedValues: Map<String, Any?>? = null
    ) : super(
        errorCode = ErrorCode.DOMAIN_RULE_VIOLATION,
        domainType = domainType,
        aggregateId = aggregateId,
        domainRule = ruleName,
        operationName = operationName,
        logLevel = LogLevel.WARN
    ) {
        ruleDescription?.let { addContext("ruleDescription", it) }
        violatedValues?.let { addContext("violatedValues", it) }
        addContext("ruleViolation", mapOf(
            "rule" to ruleName,
            "domain" to domainType
        ))
    }

    /**
     * 불변식 위반 생성자
     */
    constructor(
        invariantName: String,
        domainType: String,
        aggregateId: String,
        expectedCondition: String,
        actualValues: Map<String, Any?>,
        operationName: String? = null
    ) : super(
        errorCode = ErrorCode.AGGREGATE_CONSISTENCY_VIOLATION,
        domainType = domainType,
        aggregateId = aggregateId,
        domainRule = invariantName,
        operationName = operationName,
        logLevel = LogLevel.ERROR // 불변식 위반은 더 심각
    ) {
        addContext("invariantViolation", mapOf(
            "invariant" to invariantName,
            "expected" to expectedCondition,
            "actual" to actualValues
        ))
    }

    /**
     * 비즈니스 정책 위반 생성자
     */
    constructor(
        policyName: String,
        domainType: String,
        policyRule: String,
        context: Map<String, Any?>,
        aggregateId: String? = null
    ) : super(
        errorCode = ErrorCode.DOMAIN_RULE_VIOLATION,
        domainType = domainType,
        aggregateId = aggregateId,
        domainRule = policyName,
        logLevel = LogLevel.WARN
    ) {
        addContext("policyViolation", mapOf(
            "policy" to policyName,
            "rule" to policyRule,
            "context" to context
        ))
    }

    companion object {
        /**
         * 필드 값 검증 실패
         */
        fun invalidFieldValue(
            domainType: String,
            fieldName: String,
            actualValue: Any?,
            expectedRule: String,
            aggregateId: String? = null
        ): DomainRuleViolationException {
            return DomainRuleViolationException(
                ruleName = "FIELD_VALIDATION",
                domainType = domainType,
                ruleDescription = expectedRule,
                aggregateId = aggregateId,
                violatedValues = mapOf(fieldName to actualValue)
            )
        }

        /**
         * 고유성 제약 위반
         */
        fun uniqueConstraintViolation(
            domainType: String,
            fieldName: String,
            duplicateValue: Any,
            aggregateId: String? = null
        ): DomainRuleViolationException {
            return DomainRuleViolationException(
                ruleName = "UNIQUE_CONSTRAINT",
                domainType = domainType,
                ruleDescription = "$fieldName 값은 고유해야 합니다",
                aggregateId = aggregateId,
                violatedValues = mapOf(fieldName to duplicateValue)
            )
        }

        /**
         * 범위 제약 위반
         */
        fun rangeConstraintViolation(
            domainType: String,
            fieldName: String,
            actualValue: Number,
            minValue: Number?,
            maxValue: Number?,
            aggregateId: String? = null
        ): DomainRuleViolationException {
            return DomainRuleViolationException(
                ruleName = "RANGE_CONSTRAINT",
                domainType = domainType,
                ruleDescription = "$fieldName 값이 허용 범위를 벗어났습니다",
                aggregateId = aggregateId,
                violatedValues = mapOf(
                    fieldName to actualValue,
                    "minValue" to minValue,
                    "maxValue" to maxValue
                )
            )
        }

        /**
         * 길이 제약 위반
         */
        fun lengthConstraintViolation(
            domainType: String,
            fieldName: String,
            actualLength: Int,
            minLength: Int?,
            maxLength: Int?,
            aggregateId: String? = null
        ): DomainRuleViolationException {
            return DomainRuleViolationException(
                ruleName = "LENGTH_CONSTRAINT",
                domainType = domainType,
                ruleDescription = "$fieldName 길이가 허용 범위를 벗어났습니다",
                aggregateId = aggregateId,
                violatedValues = mapOf(
                    fieldName to "길이: $actualLength",
                    "minLength" to minLength,
                    "maxLength" to maxLength
                )
            )
        }

        /**
         * 형식 제약 위반
         */
        fun formatConstraintViolation(
            domainType: String,
            fieldName: String,
            actualValue: String,
            expectedFormat: String,
            aggregateId: String? = null
        ): DomainRuleViolationException {
            return DomainRuleViolationException(
                ruleName = "FORMAT_CONSTRAINT",
                domainType = domainType,
                ruleDescription = "$fieldName 형식이 올바르지 않습니다",
                aggregateId = aggregateId,
                violatedValues = mapOf(
                    fieldName to actualValue,
                    "expectedFormat" to expectedFormat
                )
            )
        }

        /**
         * 연관관계 제약 위반
         */
        fun associationConstraintViolation(
            domainType: String,
            associationName: String,
            relatedEntityType: String,
            relatedEntityId: String,
            constraintType: String,
            aggregateId: String? = null
        ): DomainRuleViolationException {
            return DomainRuleViolationException(
                ruleName = "ASSOCIATION_CONSTRAINT",
                domainType = domainType,
                ruleDescription = "$associationName 연관관계 제약 위반",
                aggregateId = aggregateId,
                violatedValues = mapOf(
                    "association" to associationName,
                    "relatedType" to relatedEntityType,
                    "relatedId" to relatedEntityId,
                    "constraintType" to constraintType
                )
            )
        }

        /**
         * 비즈니스 시간 제약 위반
         */
        fun businessTimeConstraintViolation(
            domainType: String,
            operationName: String,
            currentTime: String,
            allowedTimeRange: String,
            aggregateId: String? = null
        ): DomainRuleViolationException {
            return DomainRuleViolationException(
                ruleName = "BUSINESS_TIME_CONSTRAINT",
                domainType = domainType,
                ruleDescription = "비즈니스 시간 제약 위반",
                aggregateId = aggregateId,
                operationName = operationName,
                violatedValues = mapOf(
                    "currentTime" to currentTime,
                    "allowedRange" to allowedTimeRange
                )
            )
        }

        /**
         * 수량 제한 위반
         */
        fun quantityLimitViolation(
            domainType: String,
            itemType: String,
            currentQuantity: Int,
            maxAllowed: Int,
            aggregateId: String? = null
        ): DomainRuleViolationException {
            return DomainRuleViolationException(
                ruleName = "QUANTITY_LIMIT",
                domainType = domainType,
                ruleDescription = "$itemType 수량 제한 초과",
                aggregateId = aggregateId,
                violatedValues = mapOf(
                    "itemType" to itemType,
                    "current" to currentQuantity,
                    "maxAllowed" to maxAllowed
                )
            )
        }

        /**
         * 상태 전제조건 위반
         */
        fun statePreconditionViolation(
            domainType: String,
            operationName: String,
            currentState: String,
            requiredStates: List<String>,
            aggregateId: String? = null
        ): DomainRuleViolationException {
            return DomainRuleViolationException(
                ruleName = "STATE_PRECONDITION",
                domainType = domainType,
                ruleDescription = "$operationName 수행을 위한 상태 전제조건 위반",
                aggregateId = aggregateId,
                operationName = operationName,
                violatedValues = mapOf(
                    "currentState" to currentState,
                    "requiredStates" to requiredStates
                )
            )
        }
    }
}