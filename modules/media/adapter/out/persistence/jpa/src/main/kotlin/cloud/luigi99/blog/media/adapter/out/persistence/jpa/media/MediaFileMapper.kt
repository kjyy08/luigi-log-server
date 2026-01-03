package cloud.luigi99.blog.media.adapter.out.persistence.jpa.media

import cloud.luigi99.blog.media.domain.media.model.MediaFile
import cloud.luigi99.blog.media.domain.media.vo.FileSize
import cloud.luigi99.blog.media.domain.media.vo.MediaFileId
import cloud.luigi99.blog.media.domain.media.vo.MimeType
import cloud.luigi99.blog.media.domain.media.vo.OriginalFileName
import cloud.luigi99.blog.media.domain.media.vo.PublicUrl
import cloud.luigi99.blog.media.domain.media.vo.StorageKey

/**
 * MediaFile Domain ↔ JPA Entity 변환 Mapper
 *
 * Domain 모델과 JPA Entity 간의 변환을 담당합니다.
 */
object MediaFileMapper {
    /**
     * JPA Entity를 Domain 모델로 변환합니다.
     *
     * @param entity JPA Entity
     * @return Domain 모델
     */
    fun toDomain(entity: MediaFileJpaEntity): MediaFile =
        MediaFile.from(
            entityId = MediaFileId(entity.id),
            originalFileName = OriginalFileName(entity.originalFileName),
            mimeType = MimeType(entity.mimeType),
            fileSize = FileSize(entity.fileSize),
            storageKey = StorageKey(entity.storageKey),
            publicUrl = PublicUrl(entity.publicUrl),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    /**
     * Domain 모델을 JPA Entity로 변환합니다.
     *
     * @param mediaFile Domain 모델
     * @return JPA Entity
     */
    fun toEntity(mediaFile: MediaFile): MediaFileJpaEntity =
        MediaFileJpaEntity
            .from(
                entityId = mediaFile.entityId.value,
                originalFileName = mediaFile.originalFileName.value,
                mimeType = mediaFile.mimeType.value,
                fileSize = mediaFile.fileSize.bytes,
                storageKey = mediaFile.storageKey.value,
                publicUrl = mediaFile.publicUrl.value,
            ).apply {
                createdAt = mediaFile.createdAt
                updatedAt = mediaFile.updatedAt
            }
}
