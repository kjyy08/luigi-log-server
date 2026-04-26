package cloud.luigi99.blog.auth.credentials.adapter.`in`.web

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.auth.credentials.adapter.`in`.web.dto.ApiKeyListResponse
import cloud.luigi99.blog.auth.credentials.adapter.`in`.web.dto.CreateApiKeyRequest
import cloud.luigi99.blog.auth.credentials.adapter.`in`.web.dto.CreateApiKeyResponse
import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.ApiKeyCommandFacade
import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.CreateApiKeyUseCase
import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.RevokeApiKeyUseCase
import cloud.luigi99.blog.auth.credentials.application.port.`in`.query.ApiKeyQueryFacade
import cloud.luigi99.blog.auth.credentials.application.port.`in`.query.ListApiKeysUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/keys")
class ApiKeyController(
    private val apiKeyCommandFacade: ApiKeyCommandFacade,
    private val apiKeyQueryFacade: ApiKeyQueryFacade,
) : ApiKeyApi {
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    override fun createApiKey(
        @AuthenticationPrincipal memberId: String,
        @RequestBody request: CreateApiKeyRequest,
    ): ResponseEntity<CommonResponse<CreateApiKeyResponse>> {
        val response =
            apiKeyCommandFacade.createApiKey().execute(
                CreateApiKeyUseCase.Command(
                    ownerMemberId = memberId,
                    name = request.name,
                    scopes = request.scopes,
                    expiresAt = request.expiresAt,
                ),
            )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(CommonResponse.success(CreateApiKeyResponse.from(response)))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    override fun listApiKeys(
        @AuthenticationPrincipal memberId: String,
    ): ResponseEntity<CommonResponse<ApiKeyListResponse>> {
        val response =
            apiKeyQueryFacade.listApiKeys().execute(
                ListApiKeysUseCase.Query(ownerMemberId = memberId),
            )

        return ResponseEntity.ok(CommonResponse.success(ApiKeyListResponse.from(response)))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{apiKeyId}")
    override fun revokeApiKey(
        @AuthenticationPrincipal memberId: String,
        @PathVariable apiKeyId: UUID,
    ): ResponseEntity<Unit> {
        apiKeyCommandFacade.revokeApiKey().execute(
            RevokeApiKeyUseCase.Command(
                ownerMemberId = memberId,
                apiKeyId = apiKeyId,
            ),
        )

        return ResponseEntity.noContent().build()
    }
}
