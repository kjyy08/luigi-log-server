package cloud.luigi99.blog.content.domain.post.event

import cloud.luigi99.blog.common.domain.event.DomainEvent
import cloud.luigi99.blog.content.domain.post.vo.PostId
import cloud.luigi99.blog.content.domain.post.vo.Slug

/**
 * Post 발행 이벤트
 *
 * Post가 발행되었을 때 발행됩니다.
 *
 * @property postId 발행된 Post의 ID
 * @property slug 발행된 Post의 slug
 */
data class PostPublishedEvent(val postId: PostId, val slug: Slug) : DomainEvent
