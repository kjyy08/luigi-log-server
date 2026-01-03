package cloud.luigi99.blog.media.adapter.out.persistence.jpa.media

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * MediaFile JPA Repository
 *
 * Spring Data JPA를 사용한 미디어 파일 데이터 접근 인터페이스입니다.
 */
@Repository
interface MediaFileJpaRepository : JpaRepository<MediaFileJpaEntity, UUID> {
    /**
     * StorageKey로 미디어 파일을 조회합니다.
     *
     * @param storageKey 저장소 키
     * @return MediaFileJpaEntity 또는 null
     */
    @Query("SELECT m FROM MediaFileJpaEntity m WHERE m.storageKey = :storageKey")
    fun findByStorageKeyValue(
        @Param("storageKey") storageKey: String,
    ): MediaFileJpaEntity?
}
