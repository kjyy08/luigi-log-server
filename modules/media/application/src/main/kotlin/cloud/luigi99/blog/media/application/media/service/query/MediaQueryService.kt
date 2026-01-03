package cloud.luigi99.blog.media.application.media.service.query

import cloud.luigi99.blog.media.application.media.port.`in`.query.GetFileListUseCase
import cloud.luigi99.blog.media.application.media.port.`in`.query.GetFileUseCase
import cloud.luigi99.blog.media.application.media.port.`in`.query.MediaQueryFacade
import org.springframework.stereotype.Service

/**
 * 미디어 파일 Query Facade 구현체
 *
 * 파일 조회, 목록 조회 유스케이스를 제공합니다.
 */
@Service
class MediaQueryService(
    private val getFileUseCase: GetFileUseCase,
    private val getFileListUseCase: GetFileListUseCase,
) : MediaQueryFacade {
    override fun getFile(): GetFileUseCase = getFileUseCase

    override fun getFileList(): GetFileListUseCase = getFileListUseCase
}
