package cloud.luigi99.blog.auth.credentials.adapter.out.persistence.jpa.apikey

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ApiKeyJpaRepository : JpaRepository<ApiKeyJpaEntity, UUID> {
    fun findByOwnerMemberIdOrderByCreatedAtDesc(ownerMemberId: UUID): List<ApiKeyJpaEntity>

    fun findByKeyHash(keyHash: String): ApiKeyJpaEntity?
}
