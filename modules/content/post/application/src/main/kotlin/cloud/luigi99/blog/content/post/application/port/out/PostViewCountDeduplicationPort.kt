package cloud.luigi99.blog.content.post.application.port.out

import cloud.luigi99.blog.content.post.domain.vo.PostId

/**
 * 게시글 조회수 중복 증가 방지 포트.
 */
interface PostViewCountDeduplicationPort {
    /**
     * 같은 방문자의 같은 게시글 조회가 TTL 내 최초 조회이면 true를 반환한다.
     */
    fun isUniqueView(postId: PostId, visitorKey: String): Boolean
}
