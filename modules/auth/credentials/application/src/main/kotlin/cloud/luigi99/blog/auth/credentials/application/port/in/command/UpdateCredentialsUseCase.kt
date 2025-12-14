package cloud.luigi99.blog.auth.credentials.application.port.`in`.command

import cloud.luigi99.blog.auth.credentials.domain.enums.Role

interface UpdateCredentialsUseCase {
    fun execute(command: Command)

    data class Command(val memberId: String, val role: Role)
}
