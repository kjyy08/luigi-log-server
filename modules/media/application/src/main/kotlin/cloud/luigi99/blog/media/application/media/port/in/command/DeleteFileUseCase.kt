package cloud.luigi99.blog.media.application.media.port.`in`.command

/**
 * 파일 삭제 UseCase
 *
 * 파일 메타데이터와 Storage의 실제 파일을 삭제합니다.
 */
interface DeleteFileUseCase {
    /**
     * 파일을 삭제합니다.
     *
     * @param command 삭제 명령
     */
    fun execute(command: Command)

    /**
     * 파일 삭제 명령
     *
     * @property fileId 파일 ID
     */
    data class Command(val fileId: String)
}
