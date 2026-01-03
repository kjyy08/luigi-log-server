package cloud.luigi99.blog.media.application.media.port.out

import cloud.luigi99.blog.common.application.port.out.Repository
import cloud.luigi99.blog.media.domain.media.model.MediaFile
import cloud.luigi99.blog.media.domain.media.vo.MediaFileId
import cloud.luigi99.blog.media.domain.media.vo.StorageKey

/**
 * MediaFile Repository Port
 *
 * MediaFile Aggregate의 영속성 작업을 추상화합니다.
 */
interface MediaFileRepository : Repository<MediaFile, MediaFileId> {
    /**
     * StorageKey로 MediaFile을 조회합니다.
     *
     * @param storageKey 저장소 키
     * @return MediaFile 또는 null (존재하지 않을 경우)
     */
    fun findByStorageKey(storageKey: StorageKey): MediaFile?

    /**
     * 모든 MediaFile을 조회합니다.
     *
     * @return MediaFile 목록
     */
    fun findAll(): List<MediaFile>
}
