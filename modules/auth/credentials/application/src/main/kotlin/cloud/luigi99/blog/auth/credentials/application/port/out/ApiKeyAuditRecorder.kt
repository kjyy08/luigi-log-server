package cloud.luigi99.blog.auth.credentials.application.port.out

import cloud.luigi99.blog.auth.credentials.domain.vo.ApiKeyId
import java.time.LocalDateTime

interface ApiKeyAuditRecorder {
    fun record(
        action: String,
        apiKeyId: ApiKeyId?,
        prefix: String?,
        path: String?,
        result: String,
        createdAt: LocalDateTime = LocalDateTime.now(),
    )
}
