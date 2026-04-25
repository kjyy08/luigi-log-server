package cloud.luigi99.blog.auth.credentials.adapter.out.persistence.jpa.apikey

import cloud.luigi99.blog.auth.credentials.domain.enums.ApiKeyStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "api_key")
@DynamicUpdate
class ApiKeyJpaEntity(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "owner_member_id", nullable = false)
    val ownerMemberId: UUID,
    @Column(name = "name", nullable = false, length = 100)
    val name: String,
    @Column(name = "prefix", nullable = false, length = 32)
    val prefix: String,
    @Column(name = "key_hash", nullable = false, length = 64)
    val keyHash: String,
    @Column(name = "scopes", nullable = false, length = 255)
    val scopes: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    val status: ApiKeyStatus,
    @Column(name = "expires_at")
    val expiresAt: LocalDateTime?,
    @Column(name = "last_used_at")
    val lastUsedAt: LocalDateTime?,
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime,
)
