package cloud.luigi99.blog.content.post.adapter.out.persistence.jpa

import cloud.luigi99.blog.content.post.domain.model.Post
import cloud.luigi99.blog.content.post.domain.vo.Body
import cloud.luigi99.blog.content.post.domain.vo.PostId
import cloud.luigi99.blog.content.post.domain.vo.Slug
import cloud.luigi99.blog.content.post.domain.vo.Title
import cloud.luigi99.blog.member.domain.member.vo.MemberId

/**
 * Post Mapper
 *
 * Post 도메인 모델과 JPA 엔티티 간의 변환을 담당합니다.
 */
object PostMapper {
    /**
     * JPA 엔티티를 도메인 모델로 변환합니다.
     */
    fun toDomain(entity: PostJpaEntity): Post =
        Post.from(
            entityId = PostId(entity.id),
            memberId = MemberId(entity.memberId),
            title = Title(entity.title),
            slug = Slug(entity.slug),
            body = Body(entity.body),
            type = entity.type,
            status = entity.status,
            tags = entity.tags.toSet(),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    /**
     * 도메인 모델을 JPA 엔티티로 변환합니다.
     */
    fun toEntity(post: Post): PostJpaEntity {
        val postJpaEntity =
            PostJpaEntity
                .from(
                    entityId = post.entityId.value,
                    memberId = post.memberId.value,
                    title = post.title.value,
                    slug = post.slug.value,
                    body = post.body.value,
                    type = post.type,
                    status = post.status,
                ).apply {
                    createdAt = post.createdAt
                    updatedAt = post.updatedAt
                }

        // tags 복사
        postJpaEntity.tags.addAll(post.tags)

        return postJpaEntity
    }
}
