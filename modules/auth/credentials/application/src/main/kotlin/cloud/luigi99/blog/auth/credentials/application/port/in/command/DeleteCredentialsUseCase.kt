package cloud.luigi99.blog.auth.credentials.application.port.`in`.command

/**
 * 회원 인증 정보 삭제 유스케이스
 *
 * 회원 탈퇴 시 인증 정보를 삭제합니다.
 */
interface DeleteCredentialsUseCase {
    fun execute(command: Command)

    data class Command(val memberId: String)
}
