package cloud.luigi99.blog.member.adapter.`in`.web.profile.dto

import io.swagger.v3.oas.annotations.media.Schema

data class ProfileResponse(
    @field:Schema(description = "프로필 ID", example = "987fcdeb-51a2-43d1-b567-890123456789")
    val profileId: String,
    @field:Schema(description = "회원 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val memberId: String,
    @field:Schema(description = "닉네임", example = "CodingWizard")
    val nickname: String,
    @field:Schema(description = "자기소개", example = "안녕하세요, 백엔드 개발자입니다.")
    val bio: String?,
    @field:Schema(description = "프로필 이미지 URL", example = "https://example.com/images/profile.jpg")
    val profileImageUrl: String?,
    @field:Schema(description = "직업/직무", example = "Backend Developer")
    val jobTitle: String?,
    @field:Schema(description = "기술 스택", example = "[\"Kotlin\", \"Spring Boot\", \"JPA\"]")
    val techStack: List<String>,
    @field:Schema(description = "GitHub URL", example = "https://github.com/luigi99")
    val githubUrl: String?,
    @field:Schema(description = "연락처 이메일", example = "contact@luigi99.cloud")
    val contactEmail: String?,
    @field:Schema(description = "개인 웹사이트 URL", example = "https://luigi99.cloud")
    val websiteUrl: String?,
)
