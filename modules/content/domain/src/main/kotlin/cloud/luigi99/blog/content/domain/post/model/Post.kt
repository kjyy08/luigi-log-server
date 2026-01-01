package cloud.luigi99.blog.content.domain.post.model

import cloud.luigi99.blog.common.domain.AggregateRoot
import cloud.luigi99.blog.content.domain.post.event.PostArchivedEvent
import cloud.luigi99.blog.content.domain.post.event.PostCreatedEvent
import cloud.luigi99.blog.content.domain.post.event.PostPublishedEvent
import cloud.luigi99.blog.content.domain.post.vo.Body
import cloud.luigi99.blog.content.domain.post.vo.ContentType
import cloud.luigi99.blog.content.domain.post.vo.PostId
import cloud.luigi99.blog.content.domain.post.vo.PostStatus
import cloud.luigi99.blog.content.domain.post.vo.Slug
import cloud.luigi99.blog.content.domain.post.vo.Title
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import java.time.LocalDateTime

/**
 * Post 도메인 엔티티 (Aggregate Root)
 *
 * 블로그 글 또는 포트폴리오의 핵심 정보와 행위를 정의합니다.
 *
 * @property entityId Post ID
 * @property memberId 작성자 Member ID
 * @property title 제목
 * @property slug URL slug
 * @property body 본문 (Markdown)
 * @property type 콘텐츠 타입 (BLOG 또는 PORTFOLIO)
 * @property status 발행 상태
 * @property tags 태그 Set
 */
class Post private constructor(
    override val entityId: PostId,
    val memberId: MemberId,
    val title: Title,
    val slug: Slug,
    val body: Body,
    val type: ContentType,
    val status: PostStatus,
    val tags: Set<String>,
) : AggregateRoot<PostId>() {
    companion object {
        /**
         * 새로운 Post를 생성합니다.
         *
         * 초기 상태는 DRAFT이며, 빈 태그 Set을 가집니다.
         *
         * @param memberId 작성자 Member ID
         * @param title 제목
         * @param slug URL slug
         * @param body 본문
         * @param type 콘텐츠 타입
         * @return 생성된 Post
         */
        fun create(
            memberId: MemberId,
            title: Title,
            slug: Slug,
            body: Body,
            type: ContentType,
        ): Post {
            val post =
                Post(
                    entityId = PostId.generate(),
                    memberId = memberId,
                    title = title,
                    slug = slug,
                    body = body,
                    type = type,
                    status = PostStatus.DRAFT,
                    tags = emptySet(),
                )

            post.registerEvent(PostCreatedEvent(post.entityId, post.slug))
            return post
        }

        /**
         * 영속성 계층에서 데이터를 로드하여 도메인 엔티티를 재구성합니다.
         *
         * @param entityId Post ID
         * @param memberId 작성자 Member ID
         * @param title 제목
         * @param slug URL slug
         * @param body 본문
         * @param type 콘텐츠 타입
         * @param status 발행 상태
         * @param tags 태그 Set
         * @param createdAt 생성 시간
         * @param updatedAt 수정 시간
         * @return 재구성된 Post
         */
        fun from(
            entityId: PostId,
            memberId: MemberId,
            title: Title,
            slug: Slug,
            body: Body,
            type: ContentType,
            status: PostStatus,
            tags: Set<String>,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Post {
            val post =
                Post(
                    entityId = entityId,
                    memberId = memberId,
                    title = title,
                    slug = slug,
                    body = body,
                    type = type,
                    status = status,
                    tags = tags,
                )
            post.createdAt = createdAt
            post.updatedAt = updatedAt
            return post
        }
    }

    /**
     * 작성자인지 확인합니다.
     *
     * @param checkMemberId 확인할 Member ID
     * @return 작성자이면 true, 아니면 false
     */
    fun isOwner(checkMemberId: MemberId): Boolean = this.memberId == checkMemberId

    /**
     * Post를 발행합니다.
     *
     * 상태를 PUBLISHED로 변경합니다.
     *
     * @return 발행된 Post
     */
    fun publish(): Post {
        val published =
            Post(
                entityId = entityId,
                memberId = memberId,
                title = title,
                slug = slug,
                body = body,
                type = type,
                status = PostStatus.PUBLISHED,
                tags = tags,
            )
        published.createdAt = createdAt
        published.updatedAt = updatedAt

        published.registerEvent(PostPublishedEvent(published.entityId, published.slug))
        return published
    }

    /**
     * Post를 아카이빙합니다.
     *
     * 상태를 ARCHIVED로 변경합니다.
     *
     * @return 아카이빙된 Post
     */
    fun archive(): Post {
        val archived =
            Post(
                entityId = entityId,
                memberId = memberId,
                title = title,
                slug = slug,
                body = body,
                type = type,
                status = PostStatus.ARCHIVED,
                tags = tags,
            )
        archived.createdAt = createdAt
        archived.updatedAt = updatedAt

        archived.registerEvent(PostArchivedEvent(archived.entityId, archived.slug))
        return archived
    }

    /**
     * Post의 제목과 본문을 수정합니다.
     *
     * @param newTitle 새로운 제목
     * @param newBody 새로운 본문
     * @return 수정된 Post
     */
    fun update(newTitle: Title, newBody: Body): Post {
        val updated =
            Post(
                entityId = entityId,
                memberId = memberId,
                title = newTitle,
                slug = slug,
                body = newBody,
                type = type,
                status = status,
                tags = tags,
            )
        updated.createdAt = createdAt
        updated.updatedAt = updatedAt
        return updated
    }

    /**
     * 태그를 추가합니다.
     *
     * 이미 존재하는 태그는 중복 추가되지 않습니다.
     *
     * @param tagName 추가할 태그 이름
     * @return 태그가 추가된 Post
     */
    fun addTag(tagName: String): Post {
        require(tagName.isNotBlank()) { "Tag name cannot be blank" }
        require(tagName.length <= 50) { "Tag name must not exceed 50 characters" }

        val newTags = tags + tagName

        val withTag =
            Post(
                entityId = entityId,
                memberId = memberId,
                title = title,
                slug = slug,
                body = body,
                type = type,
                status = status,
                tags = newTags,
            )
        withTag.createdAt = createdAt
        withTag.updatedAt = updatedAt
        return withTag
    }

    /**
     * 태그를 제거합니다.
     *
     * @param tagName 제거할 태그 이름
     * @return 태그가 제거된 Post
     */
    fun removeTag(tagName: String): Post {
        val newTags = tags - tagName

        val withoutTag =
            Post(
                entityId = entityId,
                memberId = memberId,
                title = title,
                slug = slug,
                body = body,
                type = type,
                status = status,
                tags = newTags,
            )
        withoutTag.createdAt = createdAt
        withoutTag.updatedAt = updatedAt
        return withoutTag
    }
}
