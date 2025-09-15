package cloud.luigi99.blog.common.exception

import java.time.LocalDateTime

/**
 * 동시 수정 충돌 예외
 *
 * 낙관적 락(Optimistic Locking) 실패나 동시 수정으로 인한 충돌 발생 시 사용됩니다.
 * 일반적으로 버전 번호 불일치나 타임스탬프 기반 충돌에서 발생합니다.
 */
class ConcurrentModificationException : DomainException {

    /**
     * 버전 기반 충돌 생성자
     */
    constructor(
        domainType: String,
        aggregateId: String,
        expectedVersion: Long,
        actualVersion: Long,
        operationName: String? = null
    ) : super(
        errorCode = ErrorCode.CONCURRENT_MODIFICATION,
        domainType = domainType,
        aggregateId = aggregateId,
        aggregateVersion = actualVersion,
        operationName = operationName,
        logLevel = LogLevel.WARN
    ) {
        addContext("versionConflict", mapOf(
            "expected" to expectedVersion,
            "actual" to actualVersion,
            "conflictType" to "VERSION_MISMATCH"
        ))
    }

    /**
     * 타임스탬프 기반 충돌 생성자
     */
    constructor(
        domainType: String,
        aggregateId: String,
        expectedLastModified: LocalDateTime,
        actualLastModified: LocalDateTime,
        operationName: String? = null,
        modifiedBy: String? = null
    ) : super(
        errorCode = ErrorCode.CONCURRENT_MODIFICATION,
        domainType = domainType,
        aggregateId = aggregateId,
        operationName = operationName,
        logLevel = LogLevel.WARN
    ) {
        addContext("timestampConflict", mapOf(
            "expectedLastModified" to expectedLastModified.toString(),
            "actualLastModified" to actualLastModified.toString(),
            "modifiedBy" to modifiedBy,
            "conflictType" to "TIMESTAMP_MISMATCH"
        ))
    }

    /**
     * ETag 기반 충돌 생성자
     */
    constructor(
        domainType: String,
        aggregateId: String,
        expectedETag: String,
        actualETag: String,
        operationName: String? = null
    ) : super(
        errorCode = ErrorCode.CONCURRENT_MODIFICATION,
        domainType = domainType,
        aggregateId = aggregateId,
        operationName = operationName,
        logLevel = LogLevel.WARN
    ) {
        addContext("etagConflict", mapOf(
            "expectedETag" to expectedETag,
            "actualETag" to actualETag,
            "conflictType" to "ETAG_MISMATCH"
        ))
    }

    /**
     * 복합 충돌 감지 생성자
     */
    constructor(
        domainType: String,
        aggregateId: String,
        conflictDetails: ConflictDetails,
        operationName: String? = null
    ) : super(
        errorCode = ErrorCode.CONCURRENT_MODIFICATION,
        domainType = domainType,
        aggregateId = aggregateId,
        aggregateVersion = conflictDetails.version,
        operationName = operationName,
        logLevel = LogLevel.WARN
    ) {
        addContext("detailedConflict", mapOf(
            "type" to conflictDetails.type,
            "conflictedFields" to conflictDetails.conflictedFields,
            "originalValues" to conflictDetails.originalValues,
            "conflictingValues" to conflictDetails.conflictingValues,
            "lastModifiedBy" to conflictDetails.lastModifiedBy,
            "lastModifiedAt" to conflictDetails.lastModifiedAt?.toString()
        ))
    }

    /**
     * 재시도 가능 여부 판단
     */
    fun isRetryable(): Boolean {
        return when (getConflictType()) {
            "VERSION_MISMATCH", "TIMESTAMP_MISMATCH", "ETAG_MISMATCH" -> true
            "FIELD_LEVEL_CONFLICT" -> canResolveFieldConflicts()
            else -> false
        }
    }

    /**
     * 충돌 해결 제안사항 생성
     */
    fun getResolutionSuggestions(): List<String> {
        return when (getConflictType()) {
            "VERSION_MISMATCH" -> listOf(
                "최신 버전을 조회한 후 다시 시도하세요",
                "변경사항을 병합한 후 다시 시도하세요"
            )
            "TIMESTAMP_MISMATCH" -> listOf(
                "최신 데이터를 확인한 후 다시 시도하세요",
                "다른 사용자의 변경사항과 충돌이 없는지 확인하세요"
            )
            "FIELD_LEVEL_CONFLICT" -> generateFieldLevelSuggestions()
            else -> listOf("시스템 관리자에게 문의하세요")
        }
    }

    /**
     * 충돌 타입 반환
     */
    private fun getConflictType(): String {
        return context["versionConflict"]?.let { "VERSION_MISMATCH" }
            ?: context["timestampConflict"]?.let { "TIMESTAMP_MISMATCH" }
            ?: context["etagConflict"]?.let { "ETAG_MISMATCH" }
            ?: context["detailedConflict"]?.let { "FIELD_LEVEL_CONFLICT" }
            ?: "UNKNOWN"
    }

    /**
     * 필드 수준 충돌 해결 가능성 판단
     */
    private fun canResolveFieldConflicts(): Boolean {
        val conflictDetails = context["detailedConflict"] as? Map<*, *>
        val conflictedFields = conflictDetails?.get("conflictedFields") as? List<*>

        // 비즈니스 크리티컬 필드가 아닌 경우 해결 가능
        val criticalFields = setOf("status", "version", "id", "createdAt")
        return conflictedFields?.none { field ->
            criticalFields.contains(field.toString().lowercase())
        } ?: false
    }

    /**
     * 필드 수준 충돌 해결 제안 생성
     */
    private fun generateFieldLevelSuggestions(): List<String> {
        val conflictDetails = context["detailedConflict"] as? Map<*, *>
        val conflictedFields = conflictDetails?.get("conflictedFields") as? List<*>

        return conflictedFields?.map { field ->
            "필드 '$field'의 변경사항을 검토하고 적절한 값을 선택하세요"
        }?.toList() ?: listOf("충돌된 필드들을 확인하고 수동으로 해결하세요")
    }

    companion object {
        /**
         * 사용자 프로필 동시 수정 충돌
         */
        fun userProfileConflict(
            userId: String,
            expectedVersion: Long,
            actualVersion: Long
        ): ConcurrentModificationException {
            return ConcurrentModificationException(
                domainType = "UserProfile",
                aggregateId = userId,
                expectedVersion = expectedVersion,
                actualVersion = actualVersion,
                operationName = "updateProfile"
            )
        }

        /**
         * 콘텐츠 동시 수정 충돌
         */
        fun contentConflict(
            contentId: String,
            expectedLastModified: LocalDateTime,
            actualLastModified: LocalDateTime,
            modifiedBy: String? = null
        ): ConcurrentModificationException {
            return ConcurrentModificationException(
                domainType = "Content",
                aggregateId = contentId,
                expectedLastModified = expectedLastModified,
                actualLastModified = actualLastModified,
                operationName = "updateContent",
                modifiedBy = modifiedBy
            )
        }

        /**
         * 카테고리 동시 수정 충돌
         */
        fun categoryConflict(
            categoryId: String,
            expectedETag: String,
            actualETag: String
        ): ConcurrentModificationException {
            return ConcurrentModificationException(
                domainType = "Category",
                aggregateId = categoryId,
                expectedETag = expectedETag,
                actualETag = actualETag,
                operationName = "updateCategory"
            )
        }

        /**
         * 댓글 동시 수정 충돌
         */
        fun commentConflict(
            commentId: String,
            conflictDetails: ConflictDetails
        ): ConcurrentModificationException {
            return ConcurrentModificationException(
                domainType = "Comment",
                aggregateId = commentId,
                conflictDetails = conflictDetails,
                operationName = "updateComment"
            )
        }
    }

    /**
     * 상세 충돌 정보 클래스
     */
    data class ConflictDetails(
        val type: String,
        val version: Long?,
        val conflictedFields: List<String>,
        val originalValues: Map<String, Any?>,
        val conflictingValues: Map<String, Any?>,
        val lastModifiedBy: String?,
        val lastModifiedAt: LocalDateTime?
    )
}