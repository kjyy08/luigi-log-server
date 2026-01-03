package cloud.luigi99.blog.media.application.media.service.query

import cloud.luigi99.blog.media.application.media.port.`in`.query.GetFileUseCase
import cloud.luigi99.blog.media.application.media.port.out.MediaFileRepository
import cloud.luigi99.blog.media.domain.media.exception.FileNotFoundException
import cloud.luigi99.blog.media.domain.media.vo.MediaFileId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 파일 조회 유스케이스 구현체
 *
 * ID로 파일 메타데이터를 조회합니다.
 */
@Service
class GetFileService(private val mediaFileRepository: MediaFileRepository) : GetFileUseCase {
    @Transactional(readOnly = true)
    override fun execute(query: GetFileUseCase.Query): GetFileUseCase.Response {
        log.info { "Getting file: ${query.fileId}" }

        val fileId = MediaFileId.from(query.fileId)
        val mediaFile =
            mediaFileRepository.findById(fileId)
                ?: throw FileNotFoundException("파일을 찾을 수 없습니다: ${fileId.value}")

        return GetFileUseCase.Response(
            fileId =
                mediaFile.entityId.value
                    .toString(),
            originalFileName = mediaFile.originalFileName.value,
            mimeType = mediaFile.mimeType.value,
            fileSize = mediaFile.fileSize.bytes,
            storageKey = mediaFile.storageKey.value,
            publicUrl = mediaFile.publicUrl.value,
            createdAt = mediaFile.createdAt,
        )
    }
}
