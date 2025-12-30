package cloud.luigi99.blog.member.adapter.out.persistence.jpa.member

import cloud.luigi99.blog.common.application.port.out.DomainEventPublisher
import cloud.luigi99.blog.common.application.port.out.EventContextManager
import cloud.luigi99.blog.member.domain.member.event.MemberRegisteredEvent
import cloud.luigi99.blog.member.domain.member.model.Member
import cloud.luigi99.blog.member.domain.member.vo.Email
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import cloud.luigi99.blog.member.domain.member.vo.Username
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import java.util.UUID

/**
 * MemberRepositoryAdapter 테스트
 *
 * JPA를 통한 회원 데이터 영속화 및 도메인 이벤트 발행을 검증합니다.
 */
class MemberRepositoryAdapterTest :
    BehaviorSpec({

        val jpaRepository = mockk<MemberJpaRepository>()
        val eventContextManager = mockk<EventContextManager>()
        val domainEventPublisher = mockk<DomainEventPublisher>()
        val adapter = MemberRepositoryAdapter(jpaRepository, eventContextManager, domainEventPublisher)

        Given("새로운 회원을 등록하려고 할 때") {
            val memberId = MemberId.generate()
            val member =
                Member.from(
                    entityId = memberId,
                    email = Email("newuser@example.com"),
                    username = Username("newuser"),
                    profile = null,
                    createdAt = null,
                    updatedAt = null,
                )

            When("회원 정보를 저장하면") {
                val jpaEntity = MemberMapper.toEntity(member)
                val events = listOf(MemberRegisteredEvent(member.entityId, member.email))

                every { jpaRepository.save(any()) } returns jpaEntity
                every { eventContextManager.getDomainEventsAndClear() } returns events
                justRun { domainEventPublisher.publish(any()) }

                val saved = adapter.save(member)

                Then("저장된 회원 정보가 반환되어야 한다") {
                    saved shouldNotBe null
                    saved.entityId shouldBe member.entityId
                    saved.email shouldBe member.email
                    saved.username shouldBe member.username
                }

                Then("도메인 이벤트가 발행되어야 한다") {
                    verify(exactly = 1) {
                        domainEventPublisher.publish(match { it is MemberRegisteredEvent })
                    }
                }
            }
        }

        Given("기존 회원 정보를 수정하려고 할 때") {
            val memberId = MemberId.generate()
            val updatedMember =
                Member.from(
                    entityId = memberId,
                    email = Email("existing@example.com"),
                    username = Username("newname"),
                    profile = null,
                    createdAt = null,
                    updatedAt = null,
                )

            When("변경된 정보로 저장하면") {
                val jpaEntity = MemberMapper.toEntity(updatedMember)

                every { jpaRepository.save(any()) } returns jpaEntity
                every { eventContextManager.getDomainEventsAndClear() } returns emptyList()

                val saved = adapter.save(updatedMember)

                Then("업데이트된 회원 정보가 반환되어야 한다") {
                    saved.entityId shouldBe memberId
                    saved.username shouldBe Username("newname")
                }
            }
        }

        Given("데이터베이스에 회원이 존재할 때") {
            val memberId = MemberId.generate()
            val jpaEntity =
                MemberJpaEntity.from(
                    entityId = memberId.value,
                    email = "stored@example.com",
                    username = "storeduser",
                )

            When("회원 ID로 조회하면") {
                every { jpaRepository.findById(memberId.value) } returns Optional.of(jpaEntity)

                val found = adapter.findById(memberId)

                Then("해당 회원 정보가 반환되어야 한다") {
                    found shouldNotBe null
                    found?.entityId shouldBe memberId
                    found?.email shouldBe Email("stored@example.com")
                    found?.username shouldBe Username("storeduser")
                }
            }
        }

        Given("데이터베이스에 존재하지 않는 회원 ID가 주어졌을 때") {
            val nonExistentId = MemberId.generate()

            When("회원 ID로 조회하면") {
                every { jpaRepository.findById(nonExistentId.value) } returns Optional.empty()

                val found = adapter.findById(nonExistentId)

                Then("null이 반환되어야 한다") {
                    found shouldBe null
                }
            }
        }

        Given("특정 이메일로 가입한 회원이 존재할 때") {
            val email = Email("unique@example.com")
            val jpaEntity =
                MemberJpaEntity.from(
                    entityId = UUID.randomUUID(),
                    email = email.value,
                    username = "uniqueuser",
                )

            When("이메일로 회원을 조회하면") {
                every { jpaRepository.findByEmailValue(email.value) } returns jpaEntity

                val found = adapter.findByEmail(email)

                Then("해당 이메일의 회원 정보가 반환되어야 한다") {
                    found shouldNotBe null
                    found?.email shouldBe email
                }
            }
        }

        Given("특정 이메일로 가입한 회원이 없을 때") {
            val email = Email("notfound@example.com")

            When("이메일로 회원을 조회하면") {
                every { jpaRepository.findByEmailValue(email.value) } returns null

                val found = adapter.findByEmail(email)

                Then("null이 반환되어야 한다") {
                    found shouldBe null
                }
            }
        }

        Given("이미 사용 중인 이메일이 있을 때") {
            val email = Email("duplicate@example.com")

            When("이메일 중복 여부를 확인하면") {
                every { jpaRepository.existsByEmailValue(email.value) } returns true

                val exists = adapter.existsByEmail(email)

                Then("중복되었다는 결과가 반환되어야 한다") {
                    exists shouldBe true
                }
            }
        }

        Given("사용 가능한 이메일이 주어졌을 때") {
            val email = Email("available@example.com")

            When("이메일 중복 여부를 확인하면") {
                every { jpaRepository.existsByEmailValue(email.value) } returns false

                val exists = adapter.existsByEmail(email)

                Then("사용 가능하다는 결과가 반환되어야 한다") {
                    exists shouldBe false
                }
            }
        }

        Given("탈퇴하려는 회원이 존재할 때") {
            val memberId = MemberId.generate()

            When("회원 ID로 삭제를 요청하면") {
                justRun { jpaRepository.deleteById(memberId.value) }

                adapter.deleteById(memberId)

                Then("데이터베이스에서 회원이 삭제되어야 한다") {
                    verify(exactly = 1) {
                        jpaRepository.deleteById(memberId.value)
                    }
                }
            }
        }
    })
