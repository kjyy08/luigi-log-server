package cloud.luigi99.blog.member.adapter.`in`.web.profile.dto

import io.swagger.v3.oas.annotations.media.Schema

data class UpdateProfileRequest(
    @param:Schema(description = "닉네임", example = "CodingMaster")
    val nickname: String?,
    @param:Schema(description = "자기소개", example = "새로운 소개글입니다.")
    val bio: String?,
    @param:Schema(description = "프로필 이미지 URL", example = "https://example.com/images/new_profile.jpg")
    val profileImageUrl: String?,
    @param:Schema(description = "직업/직무", example = "Full Stack Developer")
    val jobTitle: String?,
    @param:Schema(description = "기술 스택", example = "[\"Java\", \"React\", \"AWS\"]")
    val techStack: List<String>?,
    @param:Schema(description = "GitHub URL", example = "https://github.com/new-user")
    val githubUrl: String?,
    @param:Schema(description = "연락처 이메일", example = "new.email@example.com")
    val contactEmail: String?,
    @param:Schema(description = "개인 웹사이트 URL", example = "https://new-website.com")
    val websiteUrl: String?,
)
