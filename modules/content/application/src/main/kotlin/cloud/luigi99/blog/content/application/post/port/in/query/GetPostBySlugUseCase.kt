package cloud.luigi99.blog.content.application.post.port.`in`.query

/**
 * Slug로 Post 조회 UseCase
 *
 * URL slug를 사용하여 Post를 조회합니다.
 */
interface GetPostBySlugUseCase {
    /**
     * Slug로 Post를 조회합니다.
     *
     * @param query 조회 쿼리
     * @return Post 정보
     */
    fun execute(query: Query): Response

    /**
     * Post 조회 쿼리
     *
     * @property username 사용자 이름
     * @property slug URL slug
     */
    data class Query(val username: String, val slug: String)

    /**
     * Post 조회 응답
     *
     * @property postId Post ID
     * @property author 작성자 정보
     * @property title 제목
     * @property slug URL slug
     * @property body 본문
     * @property type 컨텐츠 타입
     * @property status 상태
     * @property tags 태그 목록
     * @property createdAt 생성 시간
     * @property updatedAt 수정 시간
     */
    data class Response(
        val postId: String,
        val author: AuthorInfo,
        val title: String,
        val slug: String,
        val body: String,
        val type: String,
        val status: String,
        val tags: Set<String>,
        val createdAt: java.time.LocalDateTime?,
        val updatedAt: java.time.LocalDateTime?,
    )

    data class AuthorInfo(val memberId: String, val nickname: String, val profileImageUrl: String?)
}
