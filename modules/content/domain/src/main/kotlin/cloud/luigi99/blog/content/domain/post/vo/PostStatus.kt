package cloud.luigi99.blog.content.domain.post.vo

/**
 * Post 발행 상태
 *
 * Post의 현재 상태를 나타냅니다.
 */
enum class PostStatus {
    /**
     * 초안 상태
     *
     * 아직 발행되지 않은 초안 상태입니다.
     */
    DRAFT,

    /**
     * 발행됨
     *
     * 공개적으로 발행된 상태입니다.
     */
    PUBLISHED,

    /**
     * 아카이빙됨
     *
     * 발행이 중단되어 보관된 상태입니다.
     */
    ARCHIVED,
}
