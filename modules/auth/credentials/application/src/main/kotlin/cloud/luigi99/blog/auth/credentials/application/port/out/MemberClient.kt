package cloud.luigi99.blog.auth.credentials.application.port.out

interface MemberClient {
    fun execute(request: Request): Response

    data class Request(val email: String, val username: String, val profileImgUrl: String?)

    data class Response(
        val memberId: String,
        val email: String,
        val username: String,
        val profileImgUrl: String?,
    )
}
