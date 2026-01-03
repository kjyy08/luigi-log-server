package cloud.luigi99.blog.content.application.post.port.`in`.command

import java.time.LocalDateTime

/**
 * Post 수정 UseCase
 *
 * Post의 제목, 본문, 상태를 수정합니다.
 */
interface UpdatePostUseCase {
    /**
     * Post를 수정합니다.
     *
     * @param command 수정 명령
     * @return 수정된 Post 정보
     */
    fun execute(command: Command): Response

    /**
     * Post 수정 명령
     *
     * @property memberId 요청자 Member ID
     * @property postId Post ID
     * @property title 새로운 제목 (null이면 변경하지 않음)
     * @property body 새로운 본문 (null이면 변경하지 않음)
     * @property status 새로운 상태 (null이면 변경하지 않음, "PUBLISHED", "ARCHIVED", "DRAFT")
     */
    data class Command(
        val memberId: String,
        val postId: String,
        val title: String?,
        val body: String?,
        val status: String? = null,
    )

    /**
     * Post 수정 응답
     *
     * @property postId Post ID
     * @property author 작성자 정보
     * @property title 수정된 제목
     * @property slug URL slug
     * @property body 수정된 본문
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

    data class AuthorInfo(val memberId: String, val nickname: String, val profileImageUrl: String?)
}
