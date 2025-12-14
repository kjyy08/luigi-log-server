package cloud.luigi99.blog.auth.token.application.port.`in`.command

/**
 * 토큰 무효화 유스케이스
 *
 * 리프레시 토큰을 무효화하여 로그아웃을 처리합니다.
 */
interface RevokeTokenUseCase {
    fun execute(command: Command)

    data class Command(val memberId: String)
}
