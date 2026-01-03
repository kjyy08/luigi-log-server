package cloud.luigi99.blog.media.domain.media.model

import cloud.luigi99.blog.common.domain.AggregateRoot
import cloud.luigi99.blog.media.domain.media.event.MediaFileDeletedEvent
import cloud.luigi99.blog.media.domain.media.event.MediaFileUploadedEvent
import cloud.luigi99.blog.media.domain.media.vo.FileSize
import cloud.luigi99.blog.media.domain.media.vo.MediaFileId
import cloud.luigi99.blog.media.domain.media.vo.MimeType
import cloud.luigi99.blog.media.domain.media.vo.OriginalFileName
import cloud.luigi99.blog.media.domain.media.vo.PublicUrl
import cloud.luigi99.blog.media.domain.media.vo.StorageKey
import java.time.LocalDateTime

/**
 * 미디어 파일 Aggregate Root
 *
 * 블로그 포스트에 사용되는 파일의 메타데이터를 관리합니다.
 */
class MediaFile private constructor(
    override val entityId: MediaFileId,
    val originalFileName: OriginalFileName,
    val mimeType: MimeType,
    val fileSize: FileSize,
    val storageKey: StorageKey,
    val publicUrl: PublicUrl,
) : AggregateRoot<MediaFileId>() {
    companion object {
        /**
         * 새 파일 업로드 시 생성합니다.
         *
         * @param originalFileName 원본 파일명
         * @param mimeType MIME 타입
         * @param fileSize 파일 크기
         * @param storageKey 저장소 키
         * @param publicUrl 공개 URL
         * @return 생성된 MediaFile 인스턴스
         */
        fun upload(
            originalFileName: OriginalFileName,
            mimeType: MimeType,
            fileSize: FileSize,
            storageKey: StorageKey,
            publicUrl: PublicUrl,
        ): MediaFile {
            val file =
                MediaFile(
                    entityId = MediaFileId.generate(),
                    originalFileName = originalFileName,
                    mimeType = mimeType,
                    fileSize = fileSize,
                    storageKey = storageKey,
                    publicUrl = publicUrl,
                )
            file.registerEvent(
                MediaFileUploadedEvent(
                    fileId = file.entityId,
                    originalFileName = file.originalFileName,
                    fileSize = file.fileSize,
                ),
            )
            return file
        }

        /**
         * 영속성 계층에서 데이터를 로드하여 도메인 엔티티를 재구성합니다.
         */
        fun from(
            entityId: MediaFileId,
            originalFileName: OriginalFileName,
            mimeType: MimeType,
            fileSize: FileSize,
            storageKey: StorageKey,
            publicUrl: PublicUrl,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): MediaFile =
            MediaFile(
                entityId = entityId,
                originalFileName = originalFileName,
                mimeType = mimeType,
                fileSize = fileSize,
                storageKey = storageKey,
                publicUrl = publicUrl,
            ).apply {
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
    }

    /**
     * 파일을 삭제합니다 (물리적 삭제).
     */
    fun delete() {
        registerEvent(
            MediaFileDeletedEvent(
                fileId = entityId,
                storageKey = storageKey,
            ),
        )
    }
}
