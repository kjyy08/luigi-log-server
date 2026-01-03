package cloud.luigi99.blog.media.application.media.service.command

import cloud.luigi99.blog.media.application.media.port.`in`.command.DeleteFileUseCase
import cloud.luigi99.blog.media.application.media.port.`in`.command.MediaCommandFacade
import cloud.luigi99.blog.media.application.media.port.`in`.command.UploadFileUseCase
import org.springframework.stereotype.Service

/**
 * 미디어 파일 Command Facade 구현체
 *
 * 파일 업로드, 삭제 유스케이스를 제공합니다.
 */
@Service
class MediaCommandService(
    private val uploadFileUseCase: UploadFileUseCase,
    private val deleteFileUseCase: DeleteFileUseCase,
) : MediaCommandFacade {
    override fun uploadFile(): UploadFileUseCase = uploadFileUseCase

    override fun deleteFile(): DeleteFileUseCase = deleteFileUseCase
}
