package cloud.luigi99.blog.auth.credentials.adapter.out.persistence.jpa.apikey

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "api_key_audit_log")
class ApiKeyAuditLogJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    val id: UUID? = null,
    @Column(name = "action", nullable = false, length = 50)
    val action: String,
    @Column(name = "api_key_id")
    val apiKeyId: UUID?,
    @Column(name = "prefix", length = 32)
    val prefix: String?,
    @Column(name = "path", length = 255)
    val path: String?,
    @Column(name = "result", nullable = false, length = 50)
    val result: String,
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,
)
