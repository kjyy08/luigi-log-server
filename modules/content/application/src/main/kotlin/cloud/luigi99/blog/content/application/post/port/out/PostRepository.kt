package cloud.luigi99.blog.content.application.post.port.out

import cloud.luigi99.blog.common.application.port.out.Repository
import cloud.luigi99.blog.content.domain.post.model.Post
import cloud.luigi99.blog.content.domain.post.vo.ContentType
import cloud.luigi99.blog.content.domain.post.vo.PostId
import cloud.luigi99.blog.content.domain.post.vo.PostStatus
import cloud.luigi99.blog.content.domain.post.vo.Slug
import cloud.luigi99.blog.member.domain.member.vo.MemberId

/**
 * Post Repository Port
 *
 * Post Aggregate의 영속성 작업을 추상화합니다.
 */
interface PostRepository : Repository<Post, PostId> {
    /**
     * Slug로 Post를 조회합니다.
     *
     * @param slug Post의 slug
     * @return Post 또는 null (존재하지 않을 경우)
     */
    fun findBySlug(slug: Slug): Post?

    /**
     * Slug가 이미 존재하는지 확인합니다.
     *
     * @param slug 확인할 slug
     * @return 존재 여부
     */
    fun existsBySlug(slug: Slug): Boolean

    /**
     * 특정 사용자가 해당 slug를 사용하는 Post를 조회합니다.
     *
     * @param memberId 회원 ID
     * @param slug Post의 slug
     * @return Post 또는 null (존재하지 않을 경우)
     */
    fun findByMemberIdAndSlug(memberId: MemberId, slug: Slug): Post?

    /**
     * Username과 Slug로 Post를 조회합니다.
     *
     * @param username 사용자 이름
     * @param slug Post의 slug
     * @return Post 또는 null (존재하지 않을 경우)
     */
    fun findByUsernameAndSlug(username: String, slug: Slug): Post?

    /**
     * 특정 사용자가 해당 slug를 이미 사용 중인지 확인합니다.
     *
     * @param memberId 회원 ID
     * @param slug 확인할 slug
     * @return 존재 여부
     */
    fun existsByMemberIdAndSlug(memberId: MemberId, slug: Slug): Boolean

    /**
     * 상태별로 Post 목록을 조회합니다.
     *
     * @param status Post 상태
     * @return Post 목록
     */
    fun findAllByStatus(status: PostStatus): List<Post>

    /**
     * 컨텐츠 타입별로 Post 목록을 조회합니다.
     *
     * @param type 컨텐츠 타입
     * @return Post 목록
     */
    fun findAllByContentType(type: ContentType): List<Post>

    /**
     * 모든 Post를 조회합니다.
     *
     * @return Post 목록
     */
    fun findAll(): List<Post>
}
