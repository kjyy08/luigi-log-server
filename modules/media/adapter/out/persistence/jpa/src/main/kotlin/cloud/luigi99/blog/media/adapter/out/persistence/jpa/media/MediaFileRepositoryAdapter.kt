package cloud.luigi99.blog.media.adapter.out.persistence.jpa.media

import cloud.luigi99.blog.common.application.port.out.DomainEventPublisher
import cloud.luigi99.blog.common.application.port.out.EventContextManager
import cloud.luigi99.blog.media.application.media.port.out.MediaFileRepository
import cloud.luigi99.blog.media.domain.media.model.MediaFile
import cloud.luigi99.blog.media.domain.media.vo.MediaFileId
import cloud.luigi99.blog.media.domain.media.vo.StorageKey
import mu.KotlinLogging
import org.springframework.stereotype.Repository

private val log = KotlinLogging.logger {}

/**
 * 미디어 파일 저장소 어댑터 구현체
 *
 * JPA 리포지토리를 사용하여 미디어 파일 데이터를 영속화하고, 도메인 이벤트를 발행합니다.
 */
@Repository
class MediaFileRepositoryAdapter(
    private val jpaRepository: MediaFileJpaRepository,
    private val eventContextManager: EventContextManager,
    private val domainEventPublisher: DomainEventPublisher,
) : MediaFileRepository {
    /**
     * 미디어 파일 엔티티를 저장합니다.
     * 저장 후 누적된 도메인 이벤트를 발행합니다.
     *
     * @param entity 저장할 미디어 파일
     * @return 저장된 미디어 파일
     */
    override fun save(entity: MediaFile): MediaFile {
        log.debug { "Saving media file: ${entity.entityId}" }

        // 1. Domain → JPA
        val jpaEntity = MediaFileMapper.toEntity(entity)

        // 2. Save to DB
        val saved = jpaRepository.save(jpaEntity)

        // 3. Publish domain events
        val events = eventContextManager.getDomainEventsAndClear()
        events.forEach { domainEventPublisher.publish(it) }

        log.debug { "Successfully saved media file: ${saved.entityId}" }

        // 4. JPA → Domain
        return MediaFileMapper.toDomain(saved)
    }

    /**
     * ID로 미디어 파일을 조회합니다.
     *
     * @param id 미디어 파일 ID
     * @return 미디어 파일 또는 null
     */
    override fun findById(id: MediaFileId): MediaFile? {
        log.debug { "Finding media file by ID: $id" }
        return jpaRepository
            .findById(id.value)
            .map { MediaFileMapper.toDomain(it) }
            .orElse(null)
    }

    /**
     * StorageKey로 미디어 파일을 조회합니다.
     *
     * @param storageKey 저장소 키
     * @return 미디어 파일 또는 null
     */
    override fun findByStorageKey(storageKey: StorageKey): MediaFile? {
        log.debug { "Finding media file by storage key: $storageKey" }
        return jpaRepository
            .findByStorageKeyValue(storageKey.value)
            ?.let { MediaFileMapper.toDomain(it) }
    }

    /**
     * 모든 미디어 파일을 조회합니다.
     *
     * @return 미디어 파일 목록
     */
    override fun findAll(): List<MediaFile> {
        log.debug { "Finding all media files" }
        return jpaRepository.findAll().map { MediaFileMapper.toDomain(it) }
    }

    /**
     * ID로 미디어 파일을 삭제합니다.
     *
     * @param id 미디어 파일 ID
     */
    override fun deleteById(id: MediaFileId) {
        log.debug { "Deleting media file by ID: $id" }
        jpaRepository.deleteById(id.value)
    }
}
