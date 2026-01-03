package cloud.luigi99.blog.content.application.post.port.out

/**
 * 회원 정보 조회를 위한 Outbound Port
 */
interface MemberClient {
    /**
     * 회원 ID로 작성자 정보를 조회합니다.
     */
    fun getAuthor(memberId: String): Author

    /**
     * 여러 회원 ID로 작성자 정보 목록을 조회합니다.
     */
    fun getAuthors(memberIds: List<String>): Map<String, Author>

    /**
     * 작성자 정보 DTO
     */
    data class Author(val memberId: String, val nickname: String, val profileImageUrl: String?)
}
