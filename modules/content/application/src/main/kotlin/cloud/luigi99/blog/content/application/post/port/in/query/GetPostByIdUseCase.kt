package cloud.luigi99.blog.content.application.post.port.`in`.query

/**
 * ID로 Post 조회 UseCase
 *
 * Post ID를 사용하여 Post를 조회합니다.
 */
interface GetPostByIdUseCase {
    /**
     * ID로 Post를 조회합니다.
     *
     * @param query 조회 쿼리
     * @return Post 정보
     */
    fun execute(query: Query): Response

    /**
     * Post 조회 쿼리
     *
     * @property postId Post ID
     */
    data class Query(val postId: String)

    /**
     * Post 조회 응답
     *
     * @property postId Post ID
     * @property memberId 작성자 Member ID
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
        val memberId: String,
        val title: String,
        val slug: String,
        val body: String,
        val type: String,
        val status: String,
        val tags: Set<String>,
        val createdAt: java.time.LocalDateTime?,
        val updatedAt: java.time.LocalDateTime?,
    )
}
