package cloud.luigi99.blog.auth.credentials.adapter.out.persistence.jpa.apikey

import cloud.luigi99.blog.auth.credentials.application.port.out.ApiKeyAuditRecorder
import cloud.luigi99.blog.auth.credentials.domain.vo.ApiKeyId
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ApiKeyAuditRecorderAdapter(private val jpaRepository: ApiKeyAuditLogJpaRepository) : ApiKeyAuditRecorder {
    override fun record(
        action: String,
        apiKeyId: ApiKeyId?,
        prefix: String?,
        path: String?,
        result: String,
        createdAt: LocalDateTime,
    ) {
        jpaRepository.save(
            ApiKeyAuditLogJpaEntity(
                action = action,
                apiKeyId = apiKeyId?.value,
                prefix = prefix,
                path = path?.take(255),
                result = result,
                createdAt = createdAt,
            ),
        )
    }
}
