package cloud.luigi99.blog.auth.credentials.adapter.out.persistence.jpa.apikey

import cloud.luigi99.blog.auth.credentials.application.port.out.ApiKeyRepository
import cloud.luigi99.blog.auth.credentials.domain.model.ApiKey
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ApiKeyRepositoryAdapter(private val jpaRepository: ApiKeyJpaRepository) : ApiKeyRepository {
    override fun save(apiKey: ApiKey): ApiKey =
        ApiKeyMapper.toDomain(jpaRepository.save(ApiKeyMapper.toEntity(apiKey)))

    override fun findById(apiKeyId: UUID): ApiKey? =
        jpaRepository.findById(apiKeyId).map(ApiKeyMapper::toDomain).orElse(null)

    override fun findByOwnerMemberId(ownerMemberId: MemberId): List<ApiKey> =
        jpaRepository
            .findByOwnerMemberIdOrderByCreatedAtDesc(ownerMemberId.value)
            .map(ApiKeyMapper::toDomain)

    override fun findByKeyHash(keyHash: String): ApiKey? =
        jpaRepository.findByKeyHash(keyHash)?.let(ApiKeyMapper::toDomain)
}
