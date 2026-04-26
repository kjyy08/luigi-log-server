package cloud.luigi99.blog.auth.credentials.application.port.out

import cloud.luigi99.blog.auth.credentials.domain.model.ApiKey
import cloud.luigi99.blog.auth.credentials.domain.vo.ApiKeyId
import cloud.luigi99.blog.member.domain.member.vo.MemberId

interface ApiKeyRepository {
    fun save(apiKey: ApiKey): ApiKey

    fun findById(apiKeyId: ApiKeyId): ApiKey?

    fun findByOwnerMemberId(ownerMemberId: MemberId): List<ApiKey>

    fun findByKeyHash(keyHash: String): ApiKey?
}
