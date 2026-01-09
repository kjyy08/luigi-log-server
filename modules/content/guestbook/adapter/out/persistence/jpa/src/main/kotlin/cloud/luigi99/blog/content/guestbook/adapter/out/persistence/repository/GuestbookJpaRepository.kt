package cloud.luigi99.blog.content.guestbook.adapter.out.persistence.repository

import cloud.luigi99.blog.content.guestbook.adapter.out.persistence.entity.GuestbookJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * 방명록 Spring Data JPA 리포지토리
 */
@Repository
interface GuestbookJpaRepository : JpaRepository<GuestbookJpaEntity, UUID> {
    /**
     * 특정 작성자의 방명록 목록을 조회합니다.
     */
    @Query("SELECT g FROM GuestbookJpaEntity g WHERE g.authorId = :authorId ORDER BY g.createdAt DESC")
    fun findByAuthorIdOrderByCreatedAtDesc(
        @Param("authorId") authorId: UUID,
    ): List<GuestbookJpaEntity>

    /**
     * 모든 방명록을 생성일 기준 내림차순으로 조회합니다.
     */
    @Query("SELECT g FROM GuestbookJpaEntity g ORDER BY g.createdAt DESC")
    fun findAllByOrderByCreatedAtDesc(): List<GuestbookJpaEntity>
}
