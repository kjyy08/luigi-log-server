package cloud.luigi99.blog.content.comment.domain.event

import cloud.luigi99.blog.common.domain.event.DomainEvent
import cloud.luigi99.blog.content.comment.domain.vo.CommentId
import cloud.luigi99.blog.content.post.domain.post.vo.PostId
import cloud.luigi99.blog.member.domain.member.vo.MemberId

/**
 * 댓글이 생성되었을 때 발생하는 도메인 이벤트
 */
data class CommentCreatedEvent(val commentId: CommentId, val postId: PostId, val authorId: MemberId) : DomainEvent

/**
 * 댓글이 수정되었을 때 발생하는 도메인 이벤트
 */
data class CommentUpdatedEvent(val commentId: CommentId, val postId: PostId, val authorId: MemberId) : DomainEvent

/**
 * 댓글이 삭제되었을 때 발생하는 도메인 이벤트
 */
data class CommentDeletedEvent(val commentId: CommentId, val postId: PostId, val authorId: MemberId) : DomainEvent
