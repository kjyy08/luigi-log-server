package cloud.luigi99.blog.content.application.post.port.`in`.command

/**
 * Post 삭제 UseCase
 *
 * Post를 삭제합니다.
 */
interface DeletePostUseCase {
    /**
     * Post를 삭제합니다.
     *
     * @param command 삭제 명령
     */
    fun execute(command: Command)

    /**
     * Post 삭제 명령
     *
     * @property memberId 요청자 Member ID
     * @property postId 삭제할 Post ID
     */
    data class Command(val memberId: String, val postId: String)
}
