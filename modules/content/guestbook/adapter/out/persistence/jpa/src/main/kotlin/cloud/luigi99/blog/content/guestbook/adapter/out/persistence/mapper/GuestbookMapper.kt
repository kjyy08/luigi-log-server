package cloud.luigi99.blog.content.guestbook.adapter.out.persistence.mapper

import cloud.luigi99.blog.content.guestbook.adapter.out.persistence.entity.GuestbookJpaEntity
import cloud.luigi99.blog.content.guestbook.domain.model.Guestbook
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookContent
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookId
import cloud.luigi99.blog.member.domain.member.vo.MemberId

/**
 * 방명록 매퍼
 *
 * 도메인 엔티티와 JPA 엔티티 간의 변환을 담당합니다.
 */
object GuestbookMapper {
    /**
     * JPA 엔티티를 도메인 엔티티로 변환합니다.
     */
    fun toDomain(entity: GuestbookJpaEntity): Guestbook =
        Guestbook.from(
            entityId = GuestbookId(entity.id),
            authorId = MemberId(entity.authorId),
            content = GuestbookContent(entity.content),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    /**
     * 도메인 엔티티를 JPA 엔티티로 변환합니다.
     */
    fun toEntity(guestbook: Guestbook): GuestbookJpaEntity {
        val jpaEntity =
            GuestbookJpaEntity.from(
                id = guestbook.entityId.value,
                authorId = guestbook.authorId.value,
                content = guestbook.content.value,
            )
        jpaEntity.createdAt = guestbook.createdAt
        jpaEntity.updatedAt = guestbook.updatedAt
        return jpaEntity
    }
}
