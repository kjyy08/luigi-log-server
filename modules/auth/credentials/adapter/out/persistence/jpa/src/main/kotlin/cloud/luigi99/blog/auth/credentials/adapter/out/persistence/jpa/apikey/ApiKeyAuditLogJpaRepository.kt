package cloud.luigi99.blog.auth.credentials.adapter.out.persistence.jpa.apikey

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ApiKeyAuditLogJpaRepository : JpaRepository<ApiKeyAuditLogJpaEntity, UUID>
