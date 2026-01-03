package cloud.luigi99.blog.content.application.post.port.`in`.command

import java.time.LocalDateTime

/**
 * Post 생성 UseCase
 *
 * 새로운 블로그 글을 DRAFT 상태로 생성합니다.
 */
interface CreatePostUseCase {
    /**
     * Post를 생성합니다.
     *
     * @param command 생성 명령
     * @return 생성된 Post 정보
     */
    fun execute(command: Command): Response

    /**
     * Post 생성 명령
     *
     * @property memberId 작성자 Member ID
     * @property title 제목
     * @property slug URL slug
     * @property body 본문 (Markdown)
     * @property type 컨텐츠 타입 (BLOG, PORTFOLIO)
     * @property tags 태그 목록
     */
    data class Command(
        val memberId: String,
        val title: String,
        val slug: String,
        val body: String,
        val type: String,
        val tags: List<String> = emptyList(),
    )

    /**
     * Post 생성 응답
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
        val createdAt: LocalDateTime?,
        val updatedAt: LocalDateTime?,
    )

    /**
     * 작성자 정보
     */
    data class AuthorInfo(val memberId: String, val nickname: String, val profileImageUrl: String?)
}
