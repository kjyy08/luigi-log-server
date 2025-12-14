package cloud.luigi99.blog.member.application.member.port.`in`.command

interface RegisterMemberUseCase {
    fun execute(command: Command): Response

    data class Command(val email: String, val username: String)

    data class Response(val memberId: String, val email: String, val username: String)
}
