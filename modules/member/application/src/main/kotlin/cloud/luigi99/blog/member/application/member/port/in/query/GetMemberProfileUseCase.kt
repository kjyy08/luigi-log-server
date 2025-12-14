package cloud.luigi99.blog.member.application.member.port.`in`.query

interface GetMemberProfileUseCase {
    fun execute(query: Query): Response

    data class Query(val memberId: String)

    data class Response(
        val memberId: String,
        val email: String,
        val username: String,
        val profile: ProfileResponse?,
    )

    data class ProfileResponse(
        val profileId: String,
        val nickname: String,
        val bio: String?,
        val profileImageUrl: String?,
        val jobTitle: String?,
        val techStack: List<String>,
        val githubUrl: String?,
        val contactEmail: String?,
        val websiteUrl: String?,
    )
}
