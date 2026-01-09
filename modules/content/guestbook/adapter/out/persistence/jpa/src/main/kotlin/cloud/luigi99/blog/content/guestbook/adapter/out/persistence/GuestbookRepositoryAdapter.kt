package cloud.luigi99.blog.content.guestbook.adapter.out.persistence

import cloud.luigi99.blog.common.application.port.out.DomainEventPublisher
import cloud.luigi99.blog.common.application.port.out.EventContextManager
import cloud.luigi99.blog.content.guestbook.adapter.out.persistence.mapper.GuestbookMapper
import cloud.luigi99.blog.content.guestbook.adapter.out.persistence.repository.GuestbookJpaRepository
import cloud.luigi99.blog.content.guestbook.application.port.out.GuestbookRepository
import cloud.luigi99.blog.content.guestbook.domain.model.Guestbook
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Repository

private val log = KotlinLogging.logger {}

/**
 * 방명록 저장소 어댑터
 *
 * JPA 리포지토리를 사용하여 방명록 데이터를 영속화하고, 도메인 이벤트를 발행합니다.
 */
@Repository
class GuestbookRepositoryAdapter(
    private val jpaRepository: GuestbookJpaRepository,
    private val eventContextManager: EventContextManager,
    private val domainEventPublisher: DomainEventPublisher,
) : GuestbookRepository {
    /**
     * 방명록 엔티티를 저장합니다.
     */
    override fun save(entity: Guestbook): Guestbook {
        log.debug { "Saving guestbook: ${entity.entityId}" }

        val jpaEntity = GuestbookMapper.toEntity(entity)
        val saved = jpaRepository.save(jpaEntity)

        val events = eventContextManager.getDomainEventsAndClear()
        events.forEach { domainEventPublisher.publish(it) }

        log.debug { "Successfully saved guestbook: ${saved.entityId}" }

        return GuestbookMapper.toDomain(saved)
    }

    /**
     * ID로 방명록을 조회합니다.
     */
    override fun findById(id: GuestbookId): Guestbook? {
        log.debug { "Finding guestbook by ID: $id" }
        return jpaRepository
            .findById(id.value)
            .map { GuestbookMapper.toDomain(it) }
            .orElse(null)
    }

    /**
     * ID로 방명록을 삭제합니다.
     */
    override fun deleteById(id: GuestbookId) {
        log.debug { "Deleting guestbook by ID: $id" }
        jpaRepository.deleteById(id.value)
    }

    /**
     * 특정 작성자의 방명록 목록을 조회합니다.
     */
    override fun findByAuthorId(authorId: MemberId): List<Guestbook> {
        log.debug { "Finding guestbooks by author ID: $authorId" }
        return jpaRepository
            .findByAuthorIdOrderByCreatedAtDesc(authorId.value)
            .map { GuestbookMapper.toDomain(it) }
    }

    /**
     * 모든 방명록을 생성일 기준 내림차순으로 조회합니다.
     */
    override fun findAllOrderByCreatedAtDesc(): List<Guestbook> {
        log.debug { "Finding all guestbooks ordered by created date desc" }
        return jpaRepository
            .findAllByOrderByCreatedAtDesc()
            .map { GuestbookMapper.toDomain(it) }
    }
}
