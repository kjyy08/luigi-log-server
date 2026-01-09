package cloud.luigi99.blog.content.comment.adapter.out.persistence.repository

import cloud.luigi99.blog.content.comment.adapter.out.persistence.entity.CommentJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * 댓글 JPA 리포지토리
 *
 * Spring Data JPA를 사용한 댓글 데이터 접근 계층입니다.
 */
@Repository
interface CommentJpaRepository : JpaRepository<CommentJpaEntity, UUID> {
    /**
     * 게시글 ID로 댓글 목록을 조회합니다.
     */
    @Query("SELECT c FROM CommentJpaEntity c WHERE c.postId = :postId ORDER BY c.createdAt DESC")
    fun findByPostIdValue(
        @Param("postId") postId: UUID,
    ): List<CommentJpaEntity>

    /**
     * 댓글 ID와 작성자 ID로 댓글 존재 여부를 확인합니다.
     */
    @Query("SELECT COUNT(c) > 0 FROM CommentJpaEntity c WHERE c.id = :commentId AND c.authorId = :authorId")
    fun existsByIdAndAuthorIdValue(
        @Param("commentId") commentId: UUID,
        @Param("authorId") authorId: UUID,
    ): Boolean
}
