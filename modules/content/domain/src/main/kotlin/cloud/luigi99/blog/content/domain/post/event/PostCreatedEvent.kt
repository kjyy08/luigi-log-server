package cloud.luigi99.blog.content.domain.post.event

import cloud.luigi99.blog.common.domain.event.DomainEvent
import cloud.luigi99.blog.content.domain.post.vo.PostId
import cloud.luigi99.blog.content.domain.post.vo.Slug

/**
 * Post 생성 이벤트
 *
 * Post가 생성되었을 때 발행됩니다.
 *
 * @property postId 생성된 Post의 ID
 * @property slug 생성된 Post의 slug
 */
data class PostCreatedEvent(val postId: PostId, val slug: Slug) : DomainEvent
