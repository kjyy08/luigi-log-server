package cloud.luigi99.blog.auth.credentials.domain.model

import cloud.luigi99.blog.auth.credentials.domain.enums.ApiKeyScope
import cloud.luigi99.blog.auth.credentials.domain.enums.ApiKeyStatus
import cloud.luigi99.blog.auth.credentials.domain.vo.ApiKeyId
import cloud.luigi99.blog.common.domain.AggregateRoot
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ApiKeyTest {
    @Test
    fun `api key is aggregate root with value object id`() {
        val apiKey =
            ApiKey.create(
                ownerMemberId = MemberId.generate(),
                name = "Hermes publisher",
                prefix = "llk_test_123",
                keyHash = "hash",
                scopes = setOf(ApiKeyScope.POST_CREATE),
                expiresAt = null,
            )

        val aggregateRoot: AggregateRoot<ApiKeyId> = apiKey

        assertTrue(aggregateRoot.entityId.value.toString().isNotBlank())
    }

    @Test
    fun `active key becomes inactive when expired or revoked`() {
        val now = LocalDateTime.parse("2026-04-25T00:00:00")
        val apiKey =
            ApiKey.create(
                ownerMemberId = MemberId.generate(),
                name = "Hermes publisher",
                prefix = "llk_test_123",
                keyHash = "hash",
                scopes = setOf(ApiKeyScope.POST_CREATE),
                expiresAt = now.plusDays(1),
                now = now,
            )

        assertTrue(apiKey.active)
        assertFalse(apiKey.isExpired(now))

        apiKey.revoke(now.plusMinutes(1))

        assertFalse(apiKey.active)
        assertTrue(apiKey.status == ApiKeyStatus.REVOKED)
    }
}
