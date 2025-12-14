package cloud.luigi99.blog.auth.credentials.application.port.`in`.query

interface CredentialsQueryFacade {
    fun getMemberCredentials(): GetMemberCredentialsUseCase
}
