package cloud.luigi99.blog.member.application.member.port.`in`.command

interface UpdateMemberProfileUseCase {
    fun execute(command: Command): Response

    data class Command(
        val memberId: String,
        val nickname: String?,
        val bio: String?,
        val profileImageUrl: String?,
        val jobTitle: String?,
        val techStack: List<String>?,
        val githubUrl: String?,
        val contactEmail: String?,
        val websiteUrl: String?,
    )

    data class Response(
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
