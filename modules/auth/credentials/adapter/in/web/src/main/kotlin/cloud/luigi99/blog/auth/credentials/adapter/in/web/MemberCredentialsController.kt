package cloud.luigi99.blog.auth.credentials.adapter.`in`.web

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.auth.credentials.adapter.`in`.web.dto.CredentialsResponse
import cloud.luigi99.blog.auth.credentials.adapter.`in`.web.dto.UpdateCredentialsRequest
import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.CredentialsCommandFacade
import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.UpdateCredentialsUseCase
import cloud.luigi99.blog.auth.credentials.application.port.`in`.query.CredentialsQueryFacade
import cloud.luigi99.blog.auth.credentials.application.port.`in`.query.GetMemberCredentialsUseCase
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/auth/credentials")
class MemberCredentialsController(
    private val credentialsQueryFacade: CredentialsQueryFacade,
    private val credentialsCommandFacade: CredentialsCommandFacade,
) : MemberCredentialsApi {
    @GetMapping("/me")
    override fun getMemberCredentials(
        @AuthenticationPrincipal memberId: String,
    ): ResponseEntity<CommonResponse<CredentialsResponse>> {
        log.info { "Getting credentials for member: $memberId" }

        val response =
            credentialsQueryFacade.getMemberCredentials().execute(
                GetMemberCredentialsUseCase.Query(memberId = memberId),
            )

        return ResponseEntity.ok(
            CommonResponse.success(
                CredentialsResponse(
                    credentialsId = response.credentialsId,
                    memberId = response.memberId,
                    oauthProvider = response.oauthProvider,
                    oauthProviderId = response.oauthProviderId,
                    role = response.role,
                    lastLoginAt = response.lastLoginAt,
                ),
            ),
        )
    }

    @PutMapping("/{memberId}")
    @PreAuthorize("hasRole('ADMIN')")
    override fun updateCredentials(
        @PathVariable memberId: String,
        @RequestBody request: UpdateCredentialsRequest,
    ): ResponseEntity<CommonResponse<Unit>> {
        log.info { "Updating credentials for member: $memberId to role: ${request.role}" }

        credentialsCommandFacade.update().execute(
            UpdateCredentialsUseCase.Command(
                memberId = memberId,
                role = request.role,
            ),
        )

        return ResponseEntity.ok(CommonResponse.success(Unit))
    }
}
