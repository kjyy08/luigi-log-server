package cloud.luigi99.blog.auth.credentials.adapter.out.persistence.jpa.apikey

import cloud.luigi99.blog.auth.credentials.application.port.out.ApiKeyAuditLogRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
class ApiKeyAuditLogRepositoryAdapter(private val jpaRepository: ApiKeyAuditLogJpaRepository) :
    ApiKeyAuditLogRepository {
    override fun record(
        action: String,
        apiKeyId: UUID?,
        prefix: String?,
        path: String?,
        result: String,
        createdAt: LocalDateTime,
    ) {
        jpaRepository.save(
            ApiKeyAuditLogJpaEntity(
                action = action,
                apiKeyId = apiKeyId,
                prefix = prefix,
                path = path?.take(255),
                result = result,
                createdAt = createdAt,
            ),
        )
    }
}
