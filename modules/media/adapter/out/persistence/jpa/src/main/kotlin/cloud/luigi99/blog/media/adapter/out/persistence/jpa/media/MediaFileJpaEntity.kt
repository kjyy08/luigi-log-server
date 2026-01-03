package cloud.luigi99.blog.media.adapter.out.persistence.jpa.media

import cloud.luigi99.blog.adapter.persistence.jpa.JpaAggregateRoot
import cloud.luigi99.blog.media.domain.media.vo.MediaFileId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate
import java.util.UUID

/**
 * 미디어 파일 JPA Entity
 *
 * 미디어 파일 메타데이터를 데이터베이스에 저장합니다.
 */
@Entity
@Table(name = "media_file")
@DynamicUpdate
class MediaFileJpaEntity private constructor(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "original_file_name", nullable = false, length = 255)
    val originalFileName: String,
    @Column(name = "mime_type", nullable = false, length = 50)
    val mimeType: String,
    @Column(name = "file_size", nullable = false)
    val fileSize: Long,
    @Column(name = "storage_key", nullable = false, unique = true, length = 500)
    val storageKey: String,
    @Column(name = "public_url", nullable = false, length = 1000)
    val publicUrl: String,
) : JpaAggregateRoot<MediaFileId>() {
    override val entityId: MediaFileId
        get() = MediaFileId(id)

    companion object {
        /**
         * JPA Entity를 생성합니다.
         *
         * @param entityId 파일 ID
         * @param originalFileName 원본 파일명
         * @param mimeType MIME 타입
         * @param fileSize 파일 크기 (bytes)
         * @param storageKey 저장소 키
         * @param publicUrl Public URL
         * @return MediaFileJpaEntity 인스턴스
         */
        fun from(
            entityId: UUID,
            originalFileName: String,
            mimeType: String,
            fileSize: Long,
            storageKey: String,
            publicUrl: String,
        ): MediaFileJpaEntity =
            MediaFileJpaEntity(
                id = entityId,
                originalFileName = originalFileName,
                mimeType = mimeType,
                fileSize = fileSize,
                storageKey = storageKey,
                publicUrl = publicUrl,
            )
    }
}
