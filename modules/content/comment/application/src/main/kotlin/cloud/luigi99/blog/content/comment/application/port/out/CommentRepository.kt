package cloud.luigi99.blog.content.comment.application.port.out

import cloud.luigi99.blog.common.application.port.out.Repository
import cloud.luigi99.blog.content.comment.domain.model.Comment
import cloud.luigi99.blog.content.comment.domain.vo.CommentId
import cloud.luigi99.blog.content.post.domain.vo.PostId
import cloud.luigi99.blog.member.domain.member.vo.MemberId

/**
 * 댓글 저장소 포트
 *
 * 댓글 데이터의 영속화를 담당하는 포트 인터페이스입니다.
 */
interface CommentRepository : Repository<Comment, CommentId> {
    /**
     * 게시글 ID로 댓글 목록을 조회합니다.
     */
    fun findByPostId(postId: PostId): List<Comment>

    /**
     * 댓글 ID와 작성자 ID로 댓글 존재 여부를 확인합니다.
     */
    fun existsByIdAndAuthorId(commentId: CommentId, authorId: MemberId): Boolean
}
