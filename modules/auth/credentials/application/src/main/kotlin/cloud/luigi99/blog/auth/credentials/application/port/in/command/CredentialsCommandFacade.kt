package cloud.luigi99.blog.auth.credentials.application.port.`in`.command

interface CredentialsCommandFacade {
    fun login(): LoginUseCase

    fun update(): UpdateCredentialsUseCase

    fun delete(): DeleteCredentialsUseCase
}
