package cloud.luigi99.blog.content.guestbook.application.port.out

import cloud.luigi99.blog.common.application.port.out.Repository
import cloud.luigi99.blog.content.guestbook.domain.model.Guestbook
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookId
import cloud.luigi99.blog.member.domain.member.vo.MemberId

/**
 * 방명록 저장소 포트
 *
 * 방명록 도메인 엔티티의 영속화를 위한 아웃바운드 포트입니다.
 */
interface GuestbookRepository : Repository<Guestbook, GuestbookId> {
    /**
     * 특정 작성자의 방명록 목록을 조회합니다.
     */
    fun findByAuthorId(authorId: MemberId): List<Guestbook>

    /**
     * 모든 방명록을 생성일 기준 내림차순으로 조회합니다.
     */
    fun findAllOrderByCreatedAtDesc(): List<Guestbook>
}
