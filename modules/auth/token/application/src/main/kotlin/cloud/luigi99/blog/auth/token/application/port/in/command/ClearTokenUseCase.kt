package cloud.luigi99.blog.auth.token.application.port.`in`.command

/**
 * 회원의 토큰 정리 유스케이스
 *
 * 회원 탈퇴 시 해당 회원의 리프레시 토큰을 정리합니다.
 */
interface ClearTokenUseCase {
    fun execute(command: Command)

    data class Command(val memberId: String)
}
