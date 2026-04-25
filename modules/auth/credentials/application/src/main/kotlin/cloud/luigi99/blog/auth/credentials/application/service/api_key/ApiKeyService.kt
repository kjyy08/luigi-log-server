package cloud.luigi99.blog.auth.credentials.application.service.api_key

import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.ApiKeyCommandFacade
import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.CreateApiKeyUseCase
import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.RevokeApiKeyUseCase
import cloud.luigi99.blog.auth.credentials.application.port.`in`.query.ApiKeyQueryFacade
import cloud.luigi99.blog.auth.credentials.application.port.`in`.query.AuthenticateApiKeyUseCase
import cloud.luigi99.blog.auth.credentials.application.port.`in`.query.ListApiKeysUseCase
import cloud.luigi99.blog.auth.credentials.application.port.out.ApiKeyAuditLogRepository
import cloud.luigi99.blog.auth.credentials.application.port.out.ApiKeyRepository
import cloud.luigi99.blog.auth.credentials.domain.enums.ApiKeyScope
import cloud.luigi99.blog.auth.credentials.domain.model.ApiKey
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.Base64

@Service
class ApiKeyService(
    private val apiKeyRepository: ApiKeyRepository,
    private val apiKeyAuditLogRepository: ApiKeyAuditLogRepository,
) : ApiKeyCommandFacade,
    ApiKeyQueryFacade,
    CreateApiKeyUseCase,
    RevokeApiKeyUseCase,
    ListApiKeysUseCase,
    AuthenticateApiKeyUseCase {
    private val secureRandom = SecureRandom()

    override fun createApiKey(): CreateApiKeyUseCase = this

    override fun revokeApiKey(): RevokeApiKeyUseCase = this

    override fun listApiKeys(): ListApiKeysUseCase = this

    override fun authenticateApiKey(): AuthenticateApiKeyUseCase = this

    @Transactional
    override fun execute(command: CreateApiKeyUseCase.Command): CreateApiKeyUseCase.Response {
        require(command.name.isNotBlank()) { "API key name must not be blank" }
        require(command.scopes.isNotEmpty()) { "API key scopes must not be empty" }

        val secretKey = generateSecretKey()
        val apiKey =
            ApiKey.create(
                ownerMemberId = MemberId.from(command.ownerMemberId),
                name = command.name.trim(),
                prefix = secretKey.take(PREFIX_LENGTH),
                keyHash = sha256(secretKey),
                scopes = command.scopes.map { ApiKeyScope.from(it) }.toSet(),
                expiresAt = command.expiresAt,
            )
        val saved = apiKeyRepository.save(apiKey)
        apiKeyAuditLogRepository.record("CREATE", saved.id, saved.prefix, null, "SUCCESS")

        return CreateApiKeyUseCase.Response(
            id = saved.id,
            name = saved.name,
            prefix = saved.prefix,
            scopes = saved.scopes,
            status = saved.status,
            expiresAt = saved.expiresAt,
            lastUsedAt = saved.lastUsedAt,
            createdAt = saved.createdAt,
            secretKey = secretKey,
        )
    }

    @Transactional
    override fun execute(command: RevokeApiKeyUseCase.Command) {
        val apiKey = apiKeyRepository.findById(command.apiKeyId) ?: return
        if (apiKey.ownerMemberId != MemberId.from(command.ownerMemberId)) return

        apiKey.revoke()
        apiKeyRepository.save(apiKey)
        apiKeyAuditLogRepository.record("REVOKE", apiKey.id, apiKey.prefix, null, "SUCCESS")
    }

    @Transactional(readOnly = true)
    override fun execute(query: ListApiKeysUseCase.Query): ListApiKeysUseCase.Response {
        val ownerMemberId = MemberId.from(query.ownerMemberId)
        val summaries =
            apiKeyRepository.findByOwnerMemberId(ownerMemberId).map {
                ListApiKeysUseCase.ApiKeySummary(
                    id = it.id,
                    name = it.name,
                    prefix = it.prefix,
                    scopes = it.scopes,
                    status = it.status,
                    expiresAt = it.expiresAt,
                    lastUsedAt = it.lastUsedAt,
                    createdAt = it.createdAt,
                )
            }
        return ListApiKeysUseCase.Response(summaries)
    }

    @Transactional
    override fun execute(query: AuthenticateApiKeyUseCase.Query): AuthenticateApiKeyUseCase.Response? {
        val apiKey = apiKeyRepository.findByKeyHash(sha256(query.secretKey))
        if (apiKey == null || !apiKey.active) {
            apiKeyAuditLogRepository.record("AUTHENTICATE", apiKey?.id, apiKey?.prefix, query.path, "FAILURE")
            return null
        }

        apiKey.recordUsedAt(LocalDateTime.now())
        apiKeyRepository.save(apiKey)
        apiKeyAuditLogRepository.record("AUTHENTICATE", apiKey.id, apiKey.prefix, query.path, "SUCCESS")

        return AuthenticateApiKeyUseCase.Response(
            ownerMemberId = apiKey.ownerMemberId.toString(),
            authorities = apiKey.scopes.map { it.authority },
        )
    }

    private fun generateSecretKey(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return "llk_${Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)}"
    }

    private fun sha256(value: String): String =
        MessageDigest
            .getInstance("SHA-256")
            .digest(value.toByteArray())
            .joinToString("") { byte -> "%02x".format(byte) }

    companion object {
        private const val PREFIX_LENGTH = 12
    }
}
