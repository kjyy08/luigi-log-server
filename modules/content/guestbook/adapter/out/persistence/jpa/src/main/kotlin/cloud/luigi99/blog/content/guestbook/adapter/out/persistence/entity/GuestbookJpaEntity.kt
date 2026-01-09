package cloud.luigi99.blog.content.guestbook.adapter.out.persistence.entity

import cloud.luigi99.blog.adapter.persistence.jpa.JpaAggregateRoot
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate
import java.util.UUID

/**
 * 방명록 JPA 엔티티
 *
 * 방명록 도메인 엔티티의 영속화를 담당하는 JPA 엔티티입니다.
 */
@Entity
@Table(name = "guestbook")
@DynamicUpdate
class GuestbookJpaEntity private constructor(
    @Id
    @Column(name = "guestbook_id")
    val id: UUID,
    @Column(name = "author_id", nullable = false)
    val authorId: UUID,
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    val content: String,
) : JpaAggregateRoot<GuestbookId>() {
    override val entityId: GuestbookId
        get() = GuestbookId(id)

    companion object {
        /**
         * 원시 값들로부터 JPA 엔티티를 생성합니다.
         */
        fun from(id: UUID, authorId: UUID, content: String): GuestbookJpaEntity =
            GuestbookJpaEntity(id, authorId, content)
    }
}
