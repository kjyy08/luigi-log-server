package cloud.luigi99.blog.media.application.media.service.query

import cloud.luigi99.blog.media.application.media.port.`in`.query.GetFileListUseCase
import cloud.luigi99.blog.media.application.media.port.out.MediaFileRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 파일 목록 조회 유스케이스 구현체
 *
 * 모든 파일 메타데이터 목록을 조회합니다.
 */
@Service
class GetFileListService(private val mediaFileRepository: MediaFileRepository) : GetFileListUseCase {
    @Transactional(readOnly = true)
    override fun execute(query: GetFileListUseCase.Query): GetFileListUseCase.Response {
        log.info { "Getting file list" }

        val files = mediaFileRepository.findAll()

        val fileSummaries =
            files.map { file ->
                GetFileListUseCase.FileSummary(
                    fileId =
                        file.entityId.value
                            .toString(),
                    originalFileName = file.originalFileName.value,
                    mimeType = file.mimeType.value,
                    fileSize = file.fileSize.bytes,
                    publicUrl = file.publicUrl.value,
                    createdAt = file.createdAt,
                )
            }

        return GetFileListUseCase.Response(files = fileSummaries)
    }
}
