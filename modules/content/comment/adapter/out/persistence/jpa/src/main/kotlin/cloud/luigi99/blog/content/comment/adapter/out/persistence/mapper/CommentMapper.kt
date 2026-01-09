package cloud.luigi99.blog.content.comment.adapter.out.persistence.mapper

import cloud.luigi99.blog.content.comment.adapter.out.persistence.entity.CommentJpaEntity
import cloud.luigi99.blog.content.comment.domain.model.Comment
import cloud.luigi99.blog.content.comment.domain.vo.CommentContent
import cloud.luigi99.blog.content.comment.domain.vo.CommentId
import cloud.luigi99.blog.content.post.domain.vo.PostId
import cloud.luigi99.blog.member.domain.member.vo.MemberId

/**
 * 댓글 매퍼
 *
 * 도메인 모델과 JPA 엔티티 간의 변환을 담당합니다.
 */
object CommentMapper {
    /**
     * JPA 엔티티를 도메인 모델로 변환합니다.
     */
    fun toDomain(entity: CommentJpaEntity): Comment =
        Comment.from(
            entityId = CommentId(entity.id),
            postId = PostId(entity.postId),
            authorId = MemberId(entity.authorId),
            content = CommentContent(entity.content),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    /**
     * 도메인 모델을 JPA 엔티티로 변환합니다.
     */
    fun toEntity(comment: Comment): CommentJpaEntity {
        val entity =
            CommentJpaEntity.from(
                entityId = comment.entityId.value,
                postId = comment.postId.value,
                authorId = comment.authorId.value,
                content = comment.content.value,
            )
        entity.createdAt = comment.createdAt
        entity.updatedAt = comment.updatedAt
        return entity
    }
}
