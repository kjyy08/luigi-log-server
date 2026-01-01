package cloud.luigi99.blog.content.adapter.out.persistence.jpa.post

import cloud.luigi99.blog.common.application.port.out.DomainEventPublisher
import cloud.luigi99.blog.common.application.port.out.EventContextManager
import cloud.luigi99.blog.content.application.post.port.out.PostRepository
import cloud.luigi99.blog.content.domain.post.model.Post
import cloud.luigi99.blog.content.domain.post.vo.ContentType
import cloud.luigi99.blog.content.domain.post.vo.PostId
import cloud.luigi99.blog.content.domain.post.vo.PostStatus
import cloud.luigi99.blog.content.domain.post.vo.Slug
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Repository

private val log = KotlinLogging.logger {}

/**
 * Post 저장소 어댑터 구현체
 *
 * JPA 리포지토리를 사용하여 Post 데이터를 영속화하고, 도메인 이벤트를 발행합니다.
 */
@Repository
class PostRepositoryAdapter(
    private val jpaRepository: PostJpaRepository,
    private val eventContextManager: EventContextManager,
    private val domainEventPublisher: DomainEventPublisher,
) : PostRepository {
    /**
     * Post 엔티티를 저장합니다.
     * 저장 후 누적된 도메인 이벤트를 발행합니다.
     *
     * @param entity 저장할 Post 엔티티
     * @return 저장된 Post 엔티티
     */
    override fun save(entity: Post): Post {
        log.debug { "Saving post: ${entity.entityId}" }

        val postJpaEntity = PostMapper.toEntity(entity)
        val saved = jpaRepository.save(postJpaEntity)

        val events = eventContextManager.getDomainEventsAndClear()
        events.forEach { domainEventPublisher.publish(it) }

        log.debug { "Successfully saved post: ${saved.entityId}" }
        return PostMapper.toDomain(saved)
    }

    /**
     * ID로 Post를 조회합니다.
     */
    override fun findById(id: PostId): Post? {
        log.debug { "Finding post by ID: $id" }

        return jpaRepository
            .findById(id.value)
            .map { PostMapper.toDomain(it) }
            .orElse(null)
    }

    /**
     * Slug로 Post를 조회합니다.
     */
    override fun findBySlug(slug: Slug): Post? {
        log.debug { "Finding post by slug: $slug" }
        return jpaRepository.findBySlugValue(slug.value)?.let { PostMapper.toDomain(it) }
    }

    /**
     * Slug 존재 여부를 확인합니다.
     */
    override fun existsBySlug(slug: Slug): Boolean {
        log.debug { "Checking if post exists with slug: $slug" }
        return jpaRepository.existsBySlugValue(slug.value)
    }

    /**
     * 특정 사용자의 Slug로 Post를 조회합니다.
     */
    override fun findByMemberIdAndSlug(memberId: MemberId, slug: Slug): Post? {
        log.debug { "Finding post by memberId: $memberId and slug: $slug" }
        return jpaRepository
            .findByMemberIdAndSlugValue(memberId.value, slug.value)
            ?.let { PostMapper.toDomain(it) }
    }

    /**
     * Username과 Slug로 Post를 조회합니다.
     */
    override fun findByUsernameAndSlug(username: String, slug: Slug): Post? {
        log.debug { "Finding post by username: $username and slug: $slug" }
        return jpaRepository
            .findByUsernameAndSlug(username, slug.value)
            ?.let { PostMapper.toDomain(it) }
    }

    /**
     * 특정 사용자가 해당 Slug를 사용 중인지 확인합니다.
     */
    override fun existsByMemberIdAndSlug(memberId: MemberId, slug: Slug): Boolean {
        log.debug { "Checking if post exists with memberId: $memberId and slug: $slug" }
        return jpaRepository.existsByMemberIdAndSlugValue(memberId.value, slug.value)
    }

    /**
     * 상태별로 Post 목록을 조회합니다.
     */
    override fun findAllByStatus(status: PostStatus): List<Post> {
        log.debug { "Finding posts by status: $status" }
        return jpaRepository.findAllByStatus(status).map { PostMapper.toDomain(it) }
    }

    /**
     * 콘텐츠 타입별로 Post 목록을 조회합니다.
     */
    override fun findAllByContentType(type: ContentType): List<Post> {
        log.debug { "Finding posts by content type: $type" }
        return jpaRepository.findAllByType(type).map { PostMapper.toDomain(it) }
    }

    /**
     * 모든 Post를 조회합니다.
     */
    override fun findAll(): List<Post> {
        log.debug { "Finding all posts" }
        return jpaRepository.findAll().map { PostMapper.toDomain(it) }
    }

    /**
     * ID로 Post를 삭제합니다.
     */
    override fun deleteById(id: PostId) {
        log.debug { "Deleting post by ID: $id" }
        jpaRepository.deleteById(id.value)
        log.info { "Deleted post: $id" }
    }
}
