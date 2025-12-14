package cloud.luigi99.blog.member.adapter.out.persistence.jpa.member

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MemberJpaRepository : JpaRepository<MemberJpaEntity, UUID> {
    @Query("SELECT m FROM MemberJpaEntity m WHERE m.email = :email")
    fun findByEmailValue(
        @Param("email") email: String,
    ): MemberJpaEntity?

    @Query("SELECT COUNT(m) > 0 FROM MemberJpaEntity m WHERE m.email = :email")
    fun existsByEmailValue(
        @Param("email") email: String,
    ): Boolean
}
