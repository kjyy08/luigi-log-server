package cloud.luigi99.blog.media.application.media.service.command

import cloud.luigi99.blog.media.application.media.port.`in`.command.DeleteFileUseCase
import cloud.luigi99.blog.media.application.media.port.out.MediaFileRepository
import cloud.luigi99.blog.media.application.media.port.out.StoragePort
import cloud.luigi99.blog.media.domain.media.exception.FileNotFoundException
import cloud.luigi99.blog.media.domain.media.vo.MediaFileId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 파일 삭제 유스케이스 구현체
 *
 * 파일 메타데이터와 Storage의 실제 파일을 삭제합니다.
 */
@Service
class DeleteFileService(private val mediaFileRepository: MediaFileRepository, private val storagePort: StoragePort) :
    DeleteFileUseCase {
    @Transactional
    override fun execute(command: DeleteFileUseCase.Command) {
        log.info { "Deleting file: ${command.fileId}" }

        val fileId = MediaFileId.from(command.fileId)
        val mediaFile =
            mediaFileRepository.findById(fileId)
                ?: throw FileNotFoundException("파일을 찾을 수 없습니다: ${fileId.value}")

        // Storage에서 파일 삭제
        storagePort.delete(mediaFile.storageKey)

        // DB에서 메타데이터 삭제
        mediaFileRepository.deleteById(fileId)

        log.info { "Successfully deleted file: ${fileId.value}" }
    }
}
