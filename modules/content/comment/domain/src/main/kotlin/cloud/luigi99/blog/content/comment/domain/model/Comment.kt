package cloud.luigi99.blog.content.comment.domain.model

import cloud.luigi99.blog.common.domain.AggregateRoot
import cloud.luigi99.blog.content.comment.domain.event.CommentCreatedEvent
import cloud.luigi99.blog.content.comment.domain.event.CommentDeletedEvent
import cloud.luigi99.blog.content.comment.domain.event.CommentUpdatedEvent
import cloud.luigi99.blog.content.comment.domain.exception.UnauthorizedCommentAccessException
import cloud.luigi99.blog.content.comment.domain.vo.CommentContent
import cloud.luigi99.blog.content.comment.domain.vo.CommentId
import cloud.luigi99.blog.content.post.domain.post.vo.PostId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import java.time.LocalDateTime

/**
 * 댓글 애그리거트 루트
 *
 * 게시글에 대한 댓글의 생명주기와 비즈니스 로직을 관리합니다.
 */
class Comment private constructor(
    override val entityId: CommentId,
    val postId: PostId,
    val authorId: MemberId,
    val content: CommentContent,
) : AggregateRoot<CommentId>() {
    companion object {
        /**
         * 새로운 댓글을 생성합니다.
         */
        fun create(postId: PostId, authorId: MemberId, content: CommentContent): Comment {
            val comment =
                Comment(
                    entityId = CommentId.generate(),
                    postId = postId,
                    authorId = authorId,
                    content = content,
                )
            comment.registerEvent(CommentCreatedEvent(comment.entityId, comment.postId, comment.authorId))
            return comment
        }

        /**
         * 영속성 계층에서 데이터를 로드하여 도메인 엔티티를 재구성합니다.
         */
        fun from(
            entityId: CommentId,
            postId: PostId,
            authorId: MemberId,
            content: CommentContent,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Comment {
            val comment = Comment(entityId, postId, authorId, content)
            comment.createdAt = createdAt
            comment.updatedAt = updatedAt
            return comment
        }
    }

    /**
     * 댓글 내용을 수정합니다.
     *
     * @throws UnauthorizedCommentAccessException 요청자가 작성자가 아닌 경우
     */
    fun updateContent(newContent: CommentContent, requesterId: MemberId): Comment {
        if (!verifyAuthor(requesterId)) {
            throw UnauthorizedCommentAccessException("Only the author can update this comment")
        }
        val updated = Comment(entityId, postId, authorId, newContent)
        updated.createdAt = createdAt
        updated.updatedAt = updatedAt
        updated.registerEvent(CommentUpdatedEvent(entityId, postId, authorId))
        return updated
    }

    /**
     * 댓글 삭제를 위한 도메인 이벤트를 발행합니다.
     *
     * @throws UnauthorizedCommentAccessException 요청자가 작성자가 아닌 경우
     */
    fun delete(requesterId: MemberId) {
        if (!verifyAuthor(requesterId)) {
            throw UnauthorizedCommentAccessException("Only the author can delete this comment")
        }
        registerEvent(CommentDeletedEvent(entityId, postId, authorId))
    }

    /**
     * 주어진 회원 ID가 댓글 작성자인지 확인합니다.
     */
    fun verifyAuthor(memberId: MemberId): Boolean = authorId == memberId
}
