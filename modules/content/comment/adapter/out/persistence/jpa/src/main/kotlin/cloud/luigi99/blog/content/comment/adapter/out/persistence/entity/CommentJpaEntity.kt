package cloud.luigi99.blog.content.comment.adapter.out.persistence.entity

import cloud.luigi99.blog.adapter.persistence.jpa.JpaAggregateRoot
import cloud.luigi99.blog.content.comment.domain.vo.CommentId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate
import java.util.UUID

/**
 * 댓글 JPA 엔티티
 *
 * 댓글 데이터의 영속화를 담당하는 JPA 엔티티입니다.
 */
@Entity
@Table(name = "comment")
@DynamicUpdate
class CommentJpaEntity private constructor(
    @Id
    @Column(name = "comment_id")
    val id: UUID,
    @Column(name = "post_id", nullable = false)
    val postId: UUID,
    @Column(name = "author_id", nullable = false)
    val authorId: UUID,
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    val content: String,
) : JpaAggregateRoot<CommentId>() {
    override val entityId: CommentId
        get() = CommentId(id)

    companion object {
        /**
         * 댓글 JPA 엔티티를 생성합니다.
         */
        fun from(
            entityId: UUID,
            postId: UUID,
            authorId: UUID,
            content: String,
        ): CommentJpaEntity =
            CommentJpaEntity(
                id = entityId,
                postId = postId,
                authorId = authorId,
                content = content,
            )
    }
}
