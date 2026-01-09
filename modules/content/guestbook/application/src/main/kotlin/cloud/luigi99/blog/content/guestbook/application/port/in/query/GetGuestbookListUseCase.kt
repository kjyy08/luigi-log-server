package cloud.luigi99.blog.content.guestbook.application.port.`in`.query

/**
 * 방명록 목록 조회 유스케이스
 */
interface GetGuestbookListUseCase {
    /**
     * 방명록 목록을 조회합니다.
     */
    fun execute(): List<Response>

    /**
     * 방명록 목록 응답
     */
    data class Response(
        val guestbookId: String,
        val author: AuthorInfo,
        val content: String,
        val createdAt: String,
        val updatedAt: String,
    )

    /**
     * 작성자 정보
     */
    data class AuthorInfo(
        val memberId: String,
        val nickname: String,
        val profileImageUrl: String?,
        val username: String,
    )
}
