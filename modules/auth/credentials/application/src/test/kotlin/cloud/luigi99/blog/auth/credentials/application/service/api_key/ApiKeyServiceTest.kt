package cloud.luigi99.blog.auth.credentials.application.service.api_key

import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.CreateApiKeyUseCase
import cloud.luigi99.blog.auth.credentials.application.port.`in`.query.AuthenticateApiKeyUseCase
import cloud.luigi99.blog.auth.credentials.application.port.out.ApiKeyAuditLogRepository
import cloud.luigi99.blog.auth.credentials.application.port.out.ApiKeyRepository
import cloud.luigi99.blog.auth.credentials.domain.model.ApiKey
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.util.UUID

class ApiKeyServiceTest {
    private val apiKeyRepository = FakeApiKeyRepository()
    private val service = ApiKeyService(apiKeyRepository, NoopApiKeyAuditLogRepository())

    @Test
    fun `create returns secret once and persists only hash metadata`() {
        val ownerMemberId = MemberId.generate().toString()
        val response =
            service.execute(
                CreateApiKeyUseCase.Command(
                    ownerMemberId = ownerMemberId,
                    name = "Hermes publisher",
                    scopes = setOf("post:create", "media:upload"),
                    expiresAt = null,
                ),
            )

        val saved = requireNotNull(apiKeyRepository.findById(response.id))
        assertNotNull(response.secretKey)
        assertEquals(response.secretKey.take(12), saved.prefix)
        assertNotEquals(response.secretKey, saved.keyHash)
        assertFalse(response.secretKey in saved.keyHash)
    }

    @Test
    fun `authenticate grants scope authorities and updates last used at`() {
        val created =
            service.execute(
                CreateApiKeyUseCase.Command(
                    ownerMemberId = MemberId.generate().toString(),
                    name = "Hermes publisher",
                    scopes = setOf("post:create"),
                    expiresAt = null,
                ),
            )

        val authenticated =
            service.execute(
                AuthenticateApiKeyUseCase.Query(
                    secretKey = created.secretKey,
                    path = "/api/v1/posts",
                ),
            )

        assertEquals(listOf("SCOPE_post:create"), authenticated?.authorities)
        assertNotNull(apiKeyRepository.findById(created.id)?.lastUsedAt)
    }

    private class FakeApiKeyRepository : ApiKeyRepository {
        private val store = mutableMapOf<UUID, ApiKey>()

        override fun save(apiKey: ApiKey): ApiKey {
            store[apiKey.id] = apiKey
            return apiKey
        }

        override fun findById(apiKeyId: UUID): ApiKey? = store[apiKeyId]

        override fun findByOwnerMemberId(ownerMemberId: MemberId): List<ApiKey> =
            store.values.filter { it.ownerMemberId == ownerMemberId }

        override fun findByKeyHash(keyHash: String): ApiKey? = store.values.firstOrNull { it.keyHash == keyHash }
    }

    private class NoopApiKeyAuditLogRepository : ApiKeyAuditLogRepository {
        override fun record(
            action: String,
            apiKeyId: UUID?,
            prefix: String?,
            path: String?,
            result: String,
            createdAt: java.time.LocalDateTime,
        ) = Unit
    }
}
