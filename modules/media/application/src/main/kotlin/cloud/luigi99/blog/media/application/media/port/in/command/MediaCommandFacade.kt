package cloud.luigi99.blog.media.application.media.port.`in`.command

/**
 * 미디어 파일 Command Facade
 *
 * 파일 업로드, 삭제 유스케이스를 제공합니다.
 */
interface MediaCommandFacade {
    /**
     * 파일 업로드 UseCase를 반환합니다.
     */
    fun uploadFile(): UploadFileUseCase

    /**
     * 파일 삭제 UseCase를 반환합니다.
     */
    fun deleteFile(): DeleteFileUseCase
}
