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
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/profile")
class ProfileController(
    private val memberQueryFacade: MemberQueryFacade,
    private val memberCommandFacade: MemberCommandFacade,
) : ProfileApi {
    @GetMapping
    override fun getProfile(
        @AuthenticationPrincipal memberId: String,
    ): ResponseEntity<CommonResponse<ProfileResponse>> {
        log.info { "Getting profile for member: $memberId" }

        val response =
            memberQueryFacade.getMemberProfile().execute(
                GetMemberProfileUseCase.Query(memberId = memberId),
            )

        val profile =
            response.profile
                ?: throw ProfileException("Profile not found for member: $memberId")

        return ResponseEntity.ok(
            CommonResponse.success(
                ProfileResponse(
                    profileId = profile.profileId,
                    memberId = response.memberId,
                    nickname = profile.nickname,
                    bio = profile.bio,
                    profileImageUrl = profile.profileImageUrl,
                    jobTitle = profile.jobTitle,
                    techStack = profile.techStack,
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
                    jobTitle = request.jobTitle,
                    techStack = request.techStack,
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
                    jobTitle = response.jobTitle,
                    techStack = response.techStack,
                    githubUrl = response.githubUrl,
                    contactEmail = response.contactEmail,
                    websiteUrl = response.websiteUrl,
                ),
            ),
        )
    }
}
