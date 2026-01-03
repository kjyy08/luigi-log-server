package cloud.luigi99.blog.media.application.media.service.command

import cloud.luigi99.blog.media.application.media.port.`in`.command.UploadFileUseCase
import cloud.luigi99.blog.media.application.media.port.out.MediaFileRepository
import cloud.luigi99.blog.media.application.media.port.out.StoragePort
import cloud.luigi99.blog.media.domain.media.model.MediaFile
import cloud.luigi99.blog.media.domain.media.vo.FileSize
import cloud.luigi99.blog.media.domain.media.vo.MimeType
import cloud.luigi99.blog.media.domain.media.vo.OriginalFileName
import cloud.luigi99.blog.media.domain.media.vo.StorageKey
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 파일 업로드 유스케이스 구현체
 *
 * 파일을 Storage에 업로드하고 메타데이터를 저장합니다.
 */
@Service
class UploadFileService(private val mediaFileRepository: MediaFileRepository, private val storagePort: StoragePort) :
    UploadFileUseCase {
    @Transactional
    override fun execute(command: UploadFileUseCase.Command): UploadFileUseCase.Response {
        log.info { "Uploading file: ${command.originalFileName}" }

        // Value Object 생성 (검증 포함)
        val originalFileName = OriginalFileName(command.originalFileName)
        val mimeType = MimeType(command.mimeType)
        val fileSize = FileSize(command.fileSize)
        val storageKey = StorageKey.generate(originalFileName.value)

        // Storage에 업로드
        storagePort.upload(
            storageKey = storageKey,
            fileData = command.fileData,
            contentType = mimeType.value,
        )

        // Public URL 생성
        val publicUrl = storagePort.getPublicUrl(storageKey)

        // Domain 모델 생성
        val mediaFile =
            MediaFile.upload(
                originalFileName = originalFileName,
                mimeType = mimeType,
                fileSize = fileSize,
                storageKey = storageKey,
                publicUrl = publicUrl,
            )

        // 저장
        val savedFile = mediaFileRepository.save(mediaFile)

        log.info { "Successfully uploaded file: ${savedFile.entityId}" }

        return UploadFileUseCase.Response(
            fileId =
                savedFile.entityId.value
                    .toString(),
            originalFileName = savedFile.originalFileName.value,
            mimeType = savedFile.mimeType.value,
            fileSize = savedFile.fileSize.bytes,
            storageKey = savedFile.storageKey.value,
            publicUrl = savedFile.publicUrl.value,
        )
    }
}
