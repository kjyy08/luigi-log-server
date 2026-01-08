package cloud.luigi99.blog.member.adapter.`in`.web.profile

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.member.adapter.`in`.web.profile.dto.ProfileResponse
import cloud.luigi99.blog.member.adapter.`in`.web.profile.dto.UpdateProfileRequest
import cloud.luigi99.blog.member.application.member.port.`in`.command.MemberCommandFacade
import cloud.luigi99.blog.member.application.member.port.`in`.command.UpdateMemberProfileUseCase
import cloud.luigi99.blog.member.application.member.port.`in`.query.GetMemberProfileUseCase
import cloud.luigi99.blog.member.application.member.port.`in`.query.MemberQueryFacade
import cloud.luigi99.blog.member.domain.profile.exception.ProfileException
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/profiles")
class ProfileController(
    private val memberQueryFacade: MemberQueryFacade,
    private val memberCommandFacade: MemberCommandFacade,
) : ProfileApi {
    @GetMapping
    override fun getProfile(
        @RequestParam username: String,
    ): ResponseEntity<CommonResponse<ProfileResponse>> {
        log.info { "Getting profile for username: $username" }

        val response =
            memberQueryFacade.getMemberProfile().execute(
                GetMemberProfileUseCase.Query(username = username),
            )

        val profile =
            response.profile
                ?: throw ProfileException("Profile not found for username: $username")

        return ResponseEntity.ok(
            CommonResponse.success(
                ProfileResponse(
                    profileId = profile.profileId,
                    memberId = response.memberId,
                    nickname = profile.nickname,
                    bio = profile.bio,
                    profileImageUrl = profile.profileImageUrl,
                    readme = profile.readme,
                    company = profile.company,
                    location = profile.location,
                    jobTitle = profile.jobTitle,
                    githubUrl = profile.githubUrl,
                    contactEmail = profile.contactEmail,
                    websiteUrl = profile.websiteUrl,
                ),
            ),
        )
    }

    @PutMapping
    override fun updateProfile(
        @AuthenticationPrincipal memberId: String,
        @RequestBody request: UpdateProfileRequest,
    ): ResponseEntity<CommonResponse<ProfileResponse>> {
        log.info { "Updating profile for member: $memberId" }

        val response =
            memberCommandFacade.updateProfile().execute(
                UpdateMemberProfileUseCase.Command(
                    memberId = memberId,
                    nickname = request.nickname,
                    bio = request.bio,
                    profileImageUrl = request.profileImageUrl,
                    readme = request.readme,
                    company = request.company,
                    location = request.location,
                    jobTitle = request.jobTitle,
                    githubUrl = request.githubUrl,
                    contactEmail = request.contactEmail,
                    websiteUrl = request.websiteUrl,
                ),
            )

        return ResponseEntity.ok(
            CommonResponse.success(
                ProfileResponse(
                    profileId = response.profileId,
                    memberId = memberId,
                    nickname = response.nickname,
                    bio = response.bio,
                    profileImageUrl = response.profileImageUrl,
                    readme = response.readme,
                    company = response.company,
                    location = response.location,
                    jobTitle = response.jobTitle,
                    githubUrl = response.githubUrl,
                    contactEmail = response.contactEmail,
                    websiteUrl = response.websiteUrl,
                ),
            ),
        )
    }
}
