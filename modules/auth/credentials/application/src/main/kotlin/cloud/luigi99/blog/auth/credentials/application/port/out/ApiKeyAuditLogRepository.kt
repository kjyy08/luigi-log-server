package cloud.luigi99.blog.auth.credentials.application.port.out

import java.time.LocalDateTime
import java.util.UUID

interface ApiKeyAuditLogRepository {
    fun record(
        action: String,
        apiKeyId: UUID?,
        prefix: String?,
        path: String?,
        result: String,
        createdAt: LocalDateTime = LocalDateTime.now(),
    )
}
