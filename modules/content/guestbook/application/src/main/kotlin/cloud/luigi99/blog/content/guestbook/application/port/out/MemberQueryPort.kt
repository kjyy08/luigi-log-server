package cloud.luigi99.blog.content.guestbook.application.port.out

/**
 * 회원 정보 조회 클라이언트 포트
 *
 * 방명록 작성자의 회원 정보를 조회합니다.
 */
interface MemberQueryPort {
    /**
     * 회원 ID로 작성자 정보를 조회합니다.
     */
    fun getAuthor(memberId: String): Author

    /**
     * 여러 회원 ID로 작성자 정보를 조회합니다.
     */
    fun getAuthors(memberIds: List<String>): Map<String, Author>

    /**
     * 작성자 정보
     */
    data class Author(
        val memberId: String,
        val nickname: String,
        val profileImageUrl: String?,
        val username: String,
    )
}
