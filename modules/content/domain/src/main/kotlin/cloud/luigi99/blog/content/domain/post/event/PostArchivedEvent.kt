package cloud.luigi99.blog.content.domain.post.event

import cloud.luigi99.blog.common.domain.event.DomainEvent
import cloud.luigi99.blog.content.domain.post.vo.PostId
import cloud.luigi99.blog.content.domain.post.vo.Slug

/**
 * Post 아카이빙 이벤트
 *
 * Post가 아카이빙되었을 때 발행됩니다.
 *
 * @property postId 아카이빙된 Post의 ID
 * @property slug 아카이빙된 Post의 slug
 */
data class PostArchivedEvent(val postId: PostId, val slug: Slug) : DomainEvent
