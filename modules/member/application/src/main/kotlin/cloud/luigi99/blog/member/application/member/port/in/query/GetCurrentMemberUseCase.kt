package cloud.luigi99.blog.member.application.member.port.`in`.query

interface GetCurrentMemberUseCase {
    fun execute(query: Query): Response

    data class Query(val memberId: String)

    data class Response(val memberId: String, val email: String, val username: String)
}
