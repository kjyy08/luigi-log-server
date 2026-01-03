package cloud.luigi99.blog.content.application.post.port.`in`.query

/**
 * Post 목록 조회 UseCase
 *
 * 필터링 조건에 따라 Post 목록을 조회합니다.
 */
interface GetPostsUseCase {
    /**
     * Post 목록을 조회합니다.
     *
     * @param query 조회 쿼리
     * @return Post 목록
     */
    fun execute(query: Query): Response

    /**
     * Post 목록 조회 쿼리
     *
     * @property status 상태 필터 (null이면 전체)
     * @property type 컨텐츠 타입 필터 (null이면 전체)
     */
    data class Query(val status: String? = null, val type: String? = null)

    /**
     * Post 목록 조회 응답
     *
     * @property posts Post 목록
     */
    data class Response(val posts: List<PostSummary>)

    /**
     * Post 요약 정보
     *
     * @property postId Post ID
     * @property author 작성자 정보
     * @property title 제목
     * @property slug URL slug
     * @property type 컨텐츠 타입
     * @property status 상태
     * @property tags 태그 목록
     * @property createdAt 생성 시간
     */
    data class PostSummary(
        val postId: String,
        val author: AuthorInfo,
        val title: String,
        val slug: String,
        val type: String,
        val status: String,
        val tags: Set<String>,
        val createdAt: java.time.LocalDateTime?,
    )

    data class AuthorInfo(val memberId: String, val nickname: String, val profileImageUrl: String?)
}
