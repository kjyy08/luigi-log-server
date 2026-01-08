package cloud.luigi99.blog.member.application.member.port.`in`.query

interface GetMemberProfileUseCase {
    fun execute(query: Query): Response

    data class Query(val username: String)

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
        val readme: String?,
        val company: String?,
        val location: String?,
        val jobTitle: String?,
        val githubUrl: String?,
        val contactEmail: String?,
        val websiteUrl: String?,
    )
}
