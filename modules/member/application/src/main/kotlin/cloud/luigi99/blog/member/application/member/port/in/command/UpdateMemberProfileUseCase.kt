package cloud.luigi99.blog.member.application.member.port.`in`.command

interface UpdateMemberProfileUseCase {
    fun execute(command: Command): Response

    data class Command(
        val memberId: String,
        val nickname: String?,
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

    data class Response(
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
