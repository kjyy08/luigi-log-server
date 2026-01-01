package cloud.luigi99.blog.content.adapter.out.persistence.jpa.post

import cloud.luigi99.blog.content.domain.post.vo.ContentType
import cloud.luigi99.blog.content.domain.post.vo.PostStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Post JPA Repository
 *
 * Spring Data JPA를 사용한 Post 저장소입니다.
 */
@Repository
interface PostJpaRepository : JpaRepository<PostJpaEntity, UUID> {
    /**
     * Slug 값으로 Post를 조회합니다.
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.slug = :slug")
    fun findBySlugValue(
        @Param("slug") slug: String,
    ): PostJpaEntity?

    /**
     * Slug 값으로 Post 존재 여부를 확인합니다.
     */
    @Query("SELECT COUNT(p) > 0 FROM PostJpaEntity p WHERE p.slug = :slug")
    fun existsBySlugValue(
        @Param("slug") slug: String,
    ): Boolean

    /**
     * 특정 사용자의 Slug로 Post를 조회합니다.
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.memberId = :memberId AND p.slug = :slug")
    fun findByMemberIdAndSlugValue(
        @Param("memberId") memberId: UUID,
        @Param("slug") slug: String,
    ): PostJpaEntity?

    /**
     * 특정 사용자가 해당 Slug를 사용 중인지 확인합니다.
     */
    @Query("SELECT COUNT(p) > 0 FROM PostJpaEntity p WHERE p.memberId = :memberId AND p.slug = :slug")
    fun existsByMemberIdAndSlugValue(
        @Param("memberId") memberId: UUID,
        @Param("slug") slug: String,
    ): Boolean

    /**
     * 상태별로 Post 목록을 조회합니다.
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.status = :status")
    fun findAllByStatus(
        @Param("status") status: PostStatus,
    ): List<PostJpaEntity>

    /**
     * 콘텐츠 타입별로 Post 목록을 조회합니다.
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.type = :type")
    fun findAllByType(
        @Param("type") type: ContentType,
    ): List<PostJpaEntity>

    @Query(
        """
        SELECT p.* FROM post p
        INNER JOIN member m ON p.member_id = m.id
        WHERE m.username = :username AND p.slug = :slug
        """,
        nativeQuery = true,
    )
    fun findByUsernameAndSlug(
        @Param("username") username: String,
        @Param("slug") slug: String,
    ): PostJpaEntity?
}
