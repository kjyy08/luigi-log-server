package cloud.luigi99.blog.common.exception

/**
 * 잘못된 연산 시도 예외
 *
 * 현재 엔티티 상태에서 수행할 수 없는 연산을 시도했을 때 발생합니다.
 * 상태 기반 비즈니스 로직에서 자주 사용됩니다.
 */
class InvalidOperationException : DomainException {

    /**
     * 기본 생성자
     */
    constructor(
        domainType: String,
        operationName: String,
        currentState: String,
        reason: String? = null,
        aggregateId: String? = null
    ) : super(
        errorCode = ErrorCode.INVALID_OPERATION,
        domainType = domainType,
        aggregateId = aggregateId,
        operationName = operationName,
        currentState = currentState,
        logLevel = LogLevel.WARN
    ) {
        reason?.let { addContext("reason", it) }
        addContext("operationAttempt", mapOf(
            "operation" to operationName,
            "onState" to currentState
        ))
    }

    /**
     * 상태 전환 실패 생성자
     */
    constructor(
        domainType: String,
        operationName: String,
        fromState: String,
        toState: String,
        aggregateId: String? = null,
        blockingConditions: List<String>? = null
    ) : super(
        errorCode = ErrorCode.INVALID_OPERATION,
        domainType = domainType,
        aggregateId = aggregateId,
        operationName = operationName,
        currentState = fromState,
        expectedState = toState,
        logLevel = LogLevel.WARN
    ) {
        addContext("stateTransitionFailed", mapOf(
            "from" to fromState,
            "to" to toState,
            "operation" to operationName
        ))
        blockingConditions?.let {
            addContext("blockingConditions", it)
        }
    }

    /**
     * 권한 부족으로 인한 연산 실패
     */
    constructor(
        domainType: String,
        operationName: String,
        userId: String,
        requiredPermissions: List<String>,
        aggregateId: String? = null
    ) : super(
        errorCode = ErrorCode.INVALID_OPERATION,
        domainType = domainType,
        aggregateId = aggregateId,
        operationName = operationName,
        logLevel = LogLevel.WARN,
        userId = userId
    ) {
        addContext("permissionDenied", mapOf(
            "operation" to operationName,
            "requiredPermissions" to requiredPermissions,
            "resourceType" to domainType
        ))
    }

    companion object {
        /**
         * 콘텐츠 발행 불가
         */
        fun cannotPublishContent(
            contentId: String,
            currentStatus: String,
            reason: String
        ): InvalidOperationException {
            return InvalidOperationException(
                domainType = "Content",
                operationName = "publish",
                currentState = currentStatus,
                reason = reason,
                aggregateId = contentId
            )
        }

        /**
         * 사용자 비활성화 불가
         */
        fun cannotDeactivateUser(
            userId: String,
            reason: String
        ): InvalidOperationException {
            return InvalidOperationException(
                domainType = "User",
                operationName = "deactivate",
                currentState = "ACTIVE",
                reason = reason,
                aggregateId = userId
            )
        }

        /**
         * 삭제 불가 (연관 데이터 존재)
         */
        fun cannotDelete(
            domainType: String,
            entityId: String,
            dependencies: List<String>
        ): InvalidOperationException {
            return InvalidOperationException(
                domainType = domainType,
                operationName = "delete",
                currentState = "ACTIVE",
                reason = "의존성 데이터 존재",
                aggregateId = entityId
            ).apply {
                addContext("dependencies", dependencies)
            }
        }

        /**
         * 수정 불가 (이미 완료된 상태)
         */
        fun cannotModifyCompleted(
            domainType: String,
            entityId: String,
            operationName: String
        ): InvalidOperationException {
            return InvalidOperationException(
                domainType = domainType,
                operationName = operationName,
                currentState = "COMPLETED",
                reason = "완료된 상태에서는 수정할 수 없음",
                aggregateId = entityId
            )
        }

        /**
         * 권한 부족
         */
        fun insufficientPermission(
            domainType: String,
            operationName: String,
            userId: String,
            requiredRole: String
        ): InvalidOperationException {
            return InvalidOperationException(
                domainType = domainType,
                operationName = operationName,
                userId = userId,
                requiredPermissions = listOf(requiredRole)
            ).apply {
                addContext("permissionType", "ROLE_BASED")
                addContext("requiredRole", requiredRole)
            }
        }

        /**
         * 비즈니스 시간 제약
         */
        fun outsideBusinessHours(
            domainType: String,
            operationName: String,
            currentHour: Int
        ): InvalidOperationException {
            return InvalidOperationException(
                domainType = domainType,
                operationName = operationName,
                currentState = "AVAILABLE",
                reason = "비즈니스 시간 외"
            ).apply {
                addContext("timeConstraint", mapOf(
                    "currentHour" to currentHour,
                    "businessHours" to "09:00-18:00"
                ))
            }
        }

        /**
         * 리소스 한계 초과
         */
        fun resourceLimitExceeded(
            domainType: String,
            operationName: String,
            currentCount: Int,
            maxLimit: Int,
            aggregateId: String? = null
        ): InvalidOperationException {
            return InvalidOperationException(
                domainType = domainType,
                operationName = operationName,
                currentState = "AT_LIMIT",
                reason = "리소스 한계 초과",
                aggregateId = aggregateId
            ).apply {
                addContext("resourceLimit", mapOf(
                    "current" to currentCount,
                    "maximum" to maxLimit,
                    "operation" to operationName
                ))
            }
        }
    }
}