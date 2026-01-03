package cloud.luigi99.blog.media.application.media.port.`in`.query

/**
 * 미디어 파일 Query Facade
 *
 * 파일 조회, 목록 조회 유스케이스를 제공합니다.
 */
interface MediaQueryFacade {
    /**
     * 파일 조회 UseCase를 반환합니다.
     */
    fun getFile(): GetFileUseCase

    /**
     * 파일 목록 조회 UseCase를 반환합니다.
     */
    fun getFileList(): GetFileListUseCase
}
