package cloud.luigi99.blog.content.guestbook.adapter.out.persistence

import cloud.luigi99.blog.common.application.port.out.DomainEventPublisher
import cloud.luigi99.blog.common.application.port.out.EventContextManager
import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.guestbook.adapter.out.persistence.mapper.GuestbookMapper
import cloud.luigi99.blog.content.guestbook.adapter.out.persistence.repository.GuestbookJpaRepository
import cloud.luigi99.blog.content.guestbook.domain.event.GuestbookCreatedEvent
import cloud.luigi99.blog.content.guestbook.domain.model.Guestbook
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookContent
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import java.util.Optional

class GuestbookRepositoryAdapterTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("신규 방명록 정보가 생성되어 영구 저장이 필요할 때") {
            val jpaRepository = mockk<GuestbookJpaRepository>()
            val eventContextManager = mockk<EventContextManager>()
            val domainEventPublisher = mockk<DomainEventPublisher>()

            val adapter =
                GuestbookRepositoryAdapter(
                    jpaRepository,
                    eventContextManager,
                    domainEventPublisher,
                )

            val guestbook =
                Guestbook.create(
                    authorId = MemberId.generate(),
                    content = GuestbookContent("새 방명록 내용입니다."),
                )

            When("저장소를 통해 저장을 요청하면") {
                val savedEntity = GuestbookMapper.toEntity(guestbook)
                every { jpaRepository.save(any()) } returns savedEntity
                every { eventContextManager.getDomainEventsAndClear() } returns
                    listOf(
                        GuestbookCreatedEvent(guestbook.entityId, guestbook.authorId),
                    )
                every { domainEventPublisher.publish(any()) } just Runs

                val saved = adapter.save(guestbook)

                Then("방명록 정보가 정상적으로 저장되고 반환된다") {
                    saved.entityId shouldBe guestbook.entityId
                    saved.content shouldBe guestbook.content
                }

                Then("방명록 생성 이벤트가 시스템에 전파된다") {
                    verify(exactly = 1) {
                        domainEventPublisher.publish(match { it is GuestbookCreatedEvent })
                    }
                }
            }
        }

        Given("특정 ID의 방명록을 조회하려 할 때") {
            val jpaRepository = mockk<GuestbookJpaRepository>()
            val eventContextManager = mockk<EventContextManager>()
            val domainEventPublisher = mockk<DomainEventPublisher>()

            val adapter =
                GuestbookRepositoryAdapter(
                    jpaRepository,
                    eventContextManager,
                    domainEventPublisher,
                )

            val guestbook =
                Guestbook.create(
                    authorId = MemberId.generate(),
                    content = GuestbookContent("조회 테스트 방명록"),
                )
            val jpaEntity = GuestbookMapper.toEntity(guestbook)

            When("존재하는 ID로 조회하면") {
                every { jpaRepository.findById(guestbook.entityId.value) } returns Optional.of(jpaEntity)

                val found = adapter.findById(guestbook.entityId)

                Then("해당 방명록이 반환된다") {
                    found shouldBe guestbook
                }
            }

            When("존재하지 않는 ID로 조회하면") {
                val nonExistentId = GuestbookId.generate()
                every { jpaRepository.findById(nonExistentId.value) } returns Optional.empty()

                val found = adapter.findById(nonExistentId)

                Then("null이 반환된다") {
                    found shouldBe null
                }
            }
        }

        Given("특정 작성자의 방명록 목록을 조회하려 할 때") {
            val jpaRepository = mockk<GuestbookJpaRepository>()
            val eventContextManager = mockk<EventContextManager>()
            val domainEventPublisher = mockk<DomainEventPublisher>()

            val adapter =
                GuestbookRepositoryAdapter(
                    jpaRepository,
                    eventContextManager,
                    domainEventPublisher,
                )

            val authorId = MemberId.generate()
            val guestbook1 = Guestbook.create(authorId, GuestbookContent("첫 번째 방명록"))
            val guestbook2 = Guestbook.create(authorId, GuestbookContent("두 번째 방명록"))
            val jpaEntities =
                listOf(
                    GuestbookMapper.toEntity(guestbook1),
                    GuestbookMapper.toEntity(guestbook2),
                )

            When("작성자 ID로 조회하면") {
                every { jpaRepository.findByAuthorIdOrderByCreatedAtDesc(authorId.value) } returns jpaEntities

                val result = adapter.findByAuthorId(authorId)

                Then("해당 작성자의 모든 방명록이 반환된다") {
                    result.size shouldBe 2
                }
            }
        }
    })
