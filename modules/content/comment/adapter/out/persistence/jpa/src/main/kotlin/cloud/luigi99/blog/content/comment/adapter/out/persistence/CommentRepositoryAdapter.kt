package cloud.luigi99.blog.content.comment.adapter.out.persistence

import cloud.luigi99.blog.adapter.message.spring.SpringDomainEventPublisher
import cloud.luigi99.blog.adapter.message.spring.SpringEventContextManager
import cloud.luigi99.blog.content.comment.adapter.out.persistence.mapper.CommentMapper
import cloud.luigi99.blog.content.comment.adapter.out.persistence.repository.CommentJpaRepository
import cloud.luigi99.blog.content.comment.application.port.out.CommentRepository
import cloud.luigi99.blog.content.comment.domain.model.Comment
import cloud.luigi99.blog.content.comment.domain.vo.CommentId
import cloud.luigi99.blog.content.post.domain.vo.PostId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Repository

private val log = KotlinLogging.logger {}

/**
 * 댓글 저장소 어댑터
 *
 * CommentRepository 포트의 JPA 기반 구현체입니다.
 * JPA 리포지토리를 사용하여 댓글 데이터를 영속화하고 도메인 이벤트를 발행합니다.
 */
@Repository
class CommentRepositoryAdapter(
    private val jpaRepository: CommentJpaRepository,
    private val eventContextManager: SpringEventContextManager,
    private val domainEventPublisher: SpringDomainEventPublisher,
) : CommentRepository {
    /**
     * 댓글을 저장합니다.
     * 저장 후 누적된 도메인 이벤트를 발행합니다.
     */
    override fun save(entity: Comment): Comment {
        log.debug { "Saving comment: ${entity.entityId}" }

        val commentJpaEntity = CommentMapper.toEntity(entity)
        val saved = jpaRepository.save(commentJpaEntity)

        val events = eventContextManager.getDomainEventsAndClear()
        events.forEach { domainEventPublisher.publish(it) }

        log.debug { "Successfully saved comment: ${saved.entityId}" }

        return CommentMapper.toDomain(saved)
    }

    /**
     * 댓글 ID로 댓글을 조회합니다.
     */
    override fun findById(id: CommentId): Comment? {
        log.debug { "Finding comment by ID: $id" }
        return jpaRepository
            .findById(id.value)
            .map { CommentMapper.toDomain(it) }
            .orElse(null)
    }

    /**
     * 게시글 ID로 댓글 목록을 조회합니다.
     */
    override fun findByPostId(postId: PostId): List<Comment> {
        log.debug { "Finding comments by post ID: $postId" }
        return jpaRepository
            .findByPostIdValue(postId.value)
            .map { CommentMapper.toDomain(it) }
    }

    /**
     * 댓글 ID와 작성자 ID로 댓글 존재 여부를 확인합니다.
     */
    override fun existsByIdAndAuthorId(commentId: CommentId, authorId: MemberId): Boolean =
        jpaRepository.existsByIdAndAuthorIdValue(commentId.value, authorId.value)

    /**
     * 댓글 ID로 댓글을 삭제합니다.
     */
    override fun deleteById(id: CommentId) {
        log.debug { "Deleting comment by ID: $id" }
        jpaRepository.deleteById(id.value)
    }
}
