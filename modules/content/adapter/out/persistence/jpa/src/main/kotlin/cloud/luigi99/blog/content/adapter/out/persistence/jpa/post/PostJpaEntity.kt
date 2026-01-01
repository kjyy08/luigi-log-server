package cloud.luigi99.blog.content.adapter.out.persistence.jpa.post

import cloud.luigi99.blog.adapter.persistence.jpa.JpaAggregateRoot
import cloud.luigi99.blog.content.domain.post.vo.ContentType
import cloud.luigi99.blog.content.domain.post.vo.PostId
import cloud.luigi99.blog.content.domain.post.vo.PostStatus
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate
import java.util.UUID

/**
 * Post JPA 엔티티
 *
 * 블로그 글의 영속성 모델을 정의합니다.
 */
@Entity
@Table(name = "post")
@DynamicUpdate
class PostJpaEntity private constructor(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "member_id", nullable = false)
    val memberId: UUID,
    @Column(name = "title", nullable = false, length = 200)
    val title: String,
    @Column(name = "slug", nullable = false, unique = true, length = 200)
    val slug: String,
    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    val body: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    val type: ContentType,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    var status: PostStatus,
    @ElementCollection
    @CollectionTable(
        name = "post_tag",
        joinColumns = [JoinColumn(name = "post_id")],
    )
    @Column(name = "tag_name", length = 50)
    val tags: MutableSet<String> = mutableSetOf(),
) : JpaAggregateRoot<PostId>() {
    override val entityId: PostId
        get() = PostId(id)

    companion object {
        /**
         * JPA 엔티티를 생성합니다.
         */
        fun from(
            entityId: UUID,
            memberId: UUID,
            title: String,
            slug: String,
            body: String,
            type: ContentType,
            status: PostStatus,
        ): PostJpaEntity =
            PostJpaEntity(
                id = entityId,
                memberId = memberId,
                title = title,
                slug = slug,
                body = body,
                type = type,
                status = status,
            )
    }
}
