package cloud.luigi99.blog.content.domain.post.event

import cloud.luigi99.blog.common.domain.event.DomainEvent
import cloud.luigi99.blog.content.domain.post.vo.PostId
import cloud.luigi99.blog.member.domain.member.vo.MemberId

/**
 * Post 삭제 이벤트
 *
 * Post가 삭제되었을 때 발행됩니다.
 *
 * @property postId 삭제된 Post의 ID
 * @property memberId 삭제된 Post의 작성자 ID
 */
data class PostDeletedEvent(val postId: PostId, val memberId: MemberId) : DomainEvent
