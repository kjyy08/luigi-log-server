package cloud.luigi99.blog.content.post.adapter.out.persistence.jpa

import cloud.luigi99.blog.content.post.domain.vo.ContentType
import cloud.luigi99.blog.content.post.domain.vo.PostStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime
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

    @Query(
        """
        SELECT p.* FROM post p
        WHERE p.member_id = :memberId
          AND p.type = CAST(:type AS varchar)
          AND p.status = 'PUBLISHED'
          AND (
            p.created_at < :currentCreatedAt
            OR (p.created_at = :currentCreatedAt AND p.id < :currentPostId)
          )
        ORDER BY p.created_at DESC, p.id DESC
        LIMIT 1
        """,
        nativeQuery = true,
    )
    fun findPreviousPublishedPost(
        @Param("memberId") memberId: UUID,
        @Param("type") type: String,
        @Param("currentCreatedAt") currentCreatedAt: LocalDateTime,
        @Param("currentPostId") currentPostId: UUID,
    ): PostJpaEntity?

    @Query(
        """
        SELECT p.* FROM post p
        WHERE p.member_id = :memberId
          AND p.type = CAST(:type AS varchar)
          AND p.status = 'PUBLISHED'
          AND (
            p.created_at > :currentCreatedAt
            OR (p.created_at = :currentCreatedAt AND p.id > :currentPostId)
          )
        ORDER BY p.created_at ASC, p.id ASC
        LIMIT 1
        """,
        nativeQuery = true,
    )
    fun findNextPublishedPost(
        @Param("memberId") memberId: UUID,
        @Param("type") type: String,
        @Param("currentCreatedAt") currentCreatedAt: LocalDateTime,
        @Param("currentPostId") currentPostId: UUID,
    ): PostJpaEntity?

    @Query(
        """
        SELECT DISTINCT p.* FROM post p
        WHERE (:statusFilterEnabled = false OR p.status = CAST(:status AS varchar))
          AND (:typeFilterEnabled = false OR p.type = CAST(:type AS varchar))
          AND (:qFilterEnabled = false OR lower(p.title) LIKE lower(concat('%', CAST(:q AS varchar), '%'))
            OR lower(p.body) LIKE lower(concat('%', CAST(:q AS varchar), '%'))
            OR EXISTS (
                SELECT 1 FROM post_tag pt
                WHERE pt.post_id = p.id
                  AND lower(pt.tag_name) LIKE lower(concat('%', CAST(:q AS varchar), '%'))
            ))
          AND (:tagFilterEnabled = false OR EXISTS (
                SELECT 1 FROM post_tag pt
                WHERE pt.post_id = p.id
                  AND pt.tag_name = CAST(:tag AS varchar)
            ))
          AND (
            :cursorFilterEnabled = false
            OR p.created_at < :cursorCreatedAt
            OR (p.created_at = :cursorCreatedAt AND p.id < :cursorPostId)
          )
        ORDER BY p.created_at DESC, p.id DESC
        LIMIT :limit
        """,
        nativeQuery = true,
    )
    fun search(
        @Param("status") status: String?,
        @Param("type") type: String?,
        @Param("q") q: String?,
        @Param("tag") tag: String?,
        @Param("cursorCreatedAt") cursorCreatedAt: LocalDateTime?,
        @Param("cursorPostId") cursorPostId: UUID?,
        @Param("statusFilterEnabled") statusFilterEnabled: Boolean,
        @Param("typeFilterEnabled") typeFilterEnabled: Boolean,
        @Param("qFilterEnabled") qFilterEnabled: Boolean,
        @Param("tagFilterEnabled") tagFilterEnabled: Boolean,
        @Param("cursorFilterEnabled") cursorFilterEnabled: Boolean,
        @Param("limit") limit: Int,
    ): List<PostJpaEntity>

    @Query(
        """
        SELECT c.post_id AS postId, COUNT(*) AS count
        FROM comment c
        WHERE c.post_id IN (:postIds)
        GROUP BY c.post_id
        """,
        nativeQuery = true,
    )
    fun countCommentsByPostIds(
        @Param("postIds") postIds: Collection<UUID>,
    ): List<CommentCountRow>

    @Query(
        """
        SELECT CAST(p.created_at AS date) AS date, COUNT(*) AS count
        FROM post p
        WHERE p.status = 'PUBLISHED'
          AND p.created_at >= :fromDate
          AND p.created_at < :toExclusive
          AND (:type IS NULL OR p.type = CAST(:type AS varchar))
        GROUP BY CAST(p.created_at AS date)
        ORDER BY CAST(p.created_at AS date)
        """,
        nativeQuery = true,
    )
    fun contributions(
        @Param("fromDate") fromDate: LocalDateTime,
        @Param("toExclusive") toExclusive: LocalDateTime,
        @Param("type") type: String?,
    ): List<ContributionRow>

    @Modifying(clearAutomatically = false, flushAutomatically = false)
    @Query(
        """
        UPDATE post
        SET view_count = view_count + 1
        WHERE id = :postId
        """,
        nativeQuery = true,
    )
    fun incrementViewCount(
        @Param("postId") postId: UUID,
    ): Int

    interface CommentCountRow {
        fun getPostId(): UUID

        fun getCount(): Long
    }

    interface ContributionRow {
        fun getDate(): LocalDate

        fun getCount(): Long
    }
}
