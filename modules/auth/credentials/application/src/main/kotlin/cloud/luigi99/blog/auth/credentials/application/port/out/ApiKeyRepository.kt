package cloud.luigi99.blog.auth.credentials.application.port.out

import cloud.luigi99.blog.auth.credentials.domain.model.ApiKey
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import java.util.UUID

interface ApiKeyRepository {
    fun save(apiKey: ApiKey): ApiKey

    fun findById(apiKeyId: UUID): ApiKey?

    fun findByOwnerMemberId(ownerMemberId: MemberId): List<ApiKey>

    fun findByKeyHash(keyHash: String): ApiKey?
}
