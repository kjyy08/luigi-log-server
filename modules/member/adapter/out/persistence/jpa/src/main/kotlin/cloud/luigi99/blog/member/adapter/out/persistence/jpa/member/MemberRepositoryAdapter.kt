package cloud.luigi99.blog.member.adapter.out.persistence.jpa.member

import cloud.luigi99.blog.common.application.port.out.DomainEventPublisher
import cloud.luigi99.blog.common.application.port.out.EventContextManager
import cloud.luigi99.blog.member.application.member.port.out.MemberRepository
import cloud.luigi99.blog.member.domain.member.model.Member
import cloud.luigi99.blog.member.domain.member.vo.Email
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Repository

private val log = KotlinLogging.logger {}

/**
 * 회원 저장소 어댑터 구현체
 *
 * JPA 리포지토리를 사용하여 회원 데이터를 영속화하고, 도메인 이벤트를 발행합니다.
 */
@Repository
class MemberRepositoryAdapter(
    private val jpaRepository: MemberJpaRepository,
    private val eventContextManager: EventContextManager,
    private val domainEventPublisher: DomainEventPublisher,
) : MemberRepository {
    /**
     * 회원 엔티티를 저장합니다.
     * 저장 후 누적된 도메인 이벤트를 발행합니다.
     *
     * @param entity 저장할 회원 엔티티
     * @return 저장된 회원 엔티티
     */
    override fun save(entity: Member): Member {
        log.debug { "Saving member: ${entity.entityId}" }

        val memberJpaEntity = MemberMapper.toEntity(entity)
        val saved = jpaRepository.save(memberJpaEntity)

        val events = eventContextManager.getDomainEventsAndClear()
        events.forEach { domainEventPublisher.publish(it) }

        log.debug { "Successfully saved member: ${saved.entityId}" }
        return MemberMapper.toDomain(saved)
    }

    /**
     * ID로 회원을 조회합니다.
     */
    override fun findById(id: MemberId): Member? {
        log.debug { "Finding member by ID: $id" }

        return jpaRepository
            .findById(id.value)
            .map { MemberMapper.toDomain(it) }
            .orElse(null)
    }

    /**
     * 이메일로 회원을 조회합니다.
     */
    override fun findByEmail(email: Email): Member? {
        log.debug { "Finding member by email: $email" }
        return jpaRepository.findByEmailValue(email.value)?.let { MemberMapper.toDomain(it) }
    }

    /**
     * 이메일 중복 여부를 확인합니다.
     */
    override fun existsByEmail(email: Email): Boolean {
        log.debug { "Checking if member exists with email: $email" }
        return jpaRepository.existsByEmailValue(email.value)
    }

    /**
     * ID로 회원을 삭제합니다.
     */
    override fun deleteById(id: MemberId) {
        log.debug { "Deleting member by ID: $id" }
        jpaRepository.deleteById(id.value)
        log.info { "Deleted member: $id" }
    }
}
