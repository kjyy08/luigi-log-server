package cloud.luigi99.blog.content.guestbook.adapter.out.persistence.mapper

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.guestbook.domain.model.Guestbook
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookContent
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import java.time.LocalDateTime

class GuestbookMapperTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("비즈니스 로직에서 처리된 방명록 정보를 데이터베이스에 저장하려 할 때") {
            val guestbook =
                Guestbook.create(
                    authorId = MemberId.generate(),
                    content = GuestbookContent("테스트 방명록 내용입니다."),
                )

            When("데이터베이스 엔티티로 변환하면") {
                val entity = GuestbookMapper.toEntity(guestbook)

                Then("비즈니스 데이터가 영속성 모델로 정확히 매핑된다") {
                    entity.id shouldBe guestbook.entityId.value
                    entity.authorId shouldBe guestbook.authorId.value
                    entity.content shouldBe guestbook.content.value
                }
            }
        }

        Given("데이터베이스에 저장된 방명록 정보를 조회하여 비즈니스 객체로 변환할 때") {
            val guestbook =
                Guestbook.create(
                    authorId = MemberId.generate(),
                    content = GuestbookContent("테스트 방명록 내용입니다."),
                )

            When("비즈니스 모델로 변환하면") {
                val entity = GuestbookMapper.toEntity(guestbook)
                val converted = GuestbookMapper.toDomain(entity)

                Then("원본 데이터가 손실 없이 복원되어야 한다") {
                    converted.entityId shouldBe guestbook.entityId
                    converted.authorId shouldBe guestbook.authorId
                    converted.content shouldBe guestbook.content
                }
            }
        }

        Given("생성일과 수정일이 설정된 방명록을 변환할 때") {
            val createdAt = LocalDateTime.now().minusDays(3)
            val updatedAt = LocalDateTime.now().minusDays(1)
            val guestbook =
                Guestbook.from(
                    entityId = GuestbookId.generate(),
                    authorId = MemberId.generate(),
                    content = GuestbookContent("타임스탬프 테스트"),
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                )

            When("JPA 엔티티로 변환 후 다시 도메인으로 변환하면") {
                val entity = GuestbookMapper.toEntity(guestbook)
                val converted = GuestbookMapper.toDomain(entity)

                Then("타임스탬프 정보가 보존된다") {
                    converted.createdAt shouldBe createdAt
                    converted.updatedAt shouldBe updatedAt
                }
            }
        }
    })
