package cloud.luigi99.blog.member.application.member.port.`in`.command

/**
 * 회원 탈퇴 유스케이스
 *
 * 회원과 관련된 모든 데이터를 삭제합니다.
 */
interface DeleteMemberUseCase {
    fun execute(command: Command)

    data class Command(val memberId: String)
}
