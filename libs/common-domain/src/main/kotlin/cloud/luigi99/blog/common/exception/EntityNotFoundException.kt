package cloud.luigi99.blog.common.exception

/**
 * 엔티티를 찾을 수 없는 경우의 도메인 예외
 *
 * 특정 식별자로 엔티티를 조회했으나 존재하지 않을 때 발생합니다.
 * 일반적으로 클라이언트에게 404 상태코드로 변환됩니다.
 */
class EntityNotFoundException : DomainException {

    /**
     * 엔티티 타입과 식별자 기반 생성자
     */
    constructor(
        entityType: String,
        entityId: String,
        searchCriteria: Map<String, Any?>? = null
    ) : super(
        errorCode = ErrorCode.ENTITY_NOT_FOUND,
        domainType = entityType,
        aggregateId = entityId,
        logLevel = LogLevel.INFO // 일반적으로 비즈니스 로직상 정상적인 케이스
    ) {
        searchCriteria?.let { addContext("searchCriteria", it) }
        addContext("notFoundEntity", mapOf(
            "type" to entityType,
            "id" to entityId
        ))
    }

    /**
     * 복합 검색 조건 기반 생성자
     */
    constructor(
        entityType: String,
        searchCriteria: Map<String, Any?>,
        operationName: String? = null
    ) : super(
        errorCode = ErrorCode.ENTITY_NOT_FOUND,
        domainType = entityType,
        operationName = operationName,
        logLevel = LogLevel.INFO
    ) {
        addContext("searchCriteria", searchCriteria)
        addContext("searchType", "COMPLEX_CRITERIA")
    }

    /**
     * 연관 엔티티를 찾을 수 없는 경우
     */
    constructor(
        parentEntityType: String,
        parentEntityId: String,
        childEntityType: String,
        childEntityId: String
    ) : super(
        errorCode = ErrorCode.ENTITY_NOT_FOUND,
        domainType = childEntityType,
        aggregateId = childEntityId,
        logLevel = LogLevel.WARN
    ) {
        addContext("relationshipContext", mapOf(
            "parentType" to parentEntityType,
            "parentId" to parentEntityId,
            "childType" to childEntityType,
            "childId" to childEntityId
        ))
    }

    companion object {
        /**
         * 사용자 엔티티 조회 실패
         */
        fun user(userId: String): EntityNotFoundException {
            return EntityNotFoundException("User", userId).apply {
                addContext("commonCase", "USER_NOT_FOUND")
            }
        }

        /**
         * 콘텐츠 엔티티 조회 실패
         */
        fun content(contentId: String): EntityNotFoundException {
            return EntityNotFoundException("Content", contentId).apply {
                addContext("commonCase", "CONTENT_NOT_FOUND")
            }
        }

        /**
         * 카테고리 엔티티 조회 실패
         */
        fun category(categoryId: String): EntityNotFoundException {
            return EntityNotFoundException("Category", categoryId).apply {
                addContext("commonCase", "CATEGORY_NOT_FOUND")
            }
        }

        /**
         * 활성 상태 엔티티만 조회하는 경우
         */
        fun activeEntity(
            entityType: String,
            entityId: String,
            currentStatus: String
        ): EntityNotFoundException {
            return EntityNotFoundException(entityType, entityId).apply {
                addContext("activeSearchFailed", mapOf(
                    "currentStatus" to currentStatus,
                    "expectedStatus" to "ACTIVE"
                ))
            }
        }
    }
}