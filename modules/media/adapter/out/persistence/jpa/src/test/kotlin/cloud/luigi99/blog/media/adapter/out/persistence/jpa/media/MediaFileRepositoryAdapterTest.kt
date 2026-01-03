package cloud.luigi99.blog.media.adapter.out.persistence.jpa.media

import cloud.luigi99.blog.common.application.port.out.DomainEventPublisher
import cloud.luigi99.blog.common.application.port.out.EventContextManager
import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.media.domain.media.event.MediaFileUploadedEvent
import cloud.luigi99.blog.media.domain.media.model.MediaFile
import cloud.luigi99.blog.media.domain.media.vo.FileSize
import cloud.luigi99.blog.media.domain.media.vo.MediaFileId
import cloud.luigi99.blog.media.domain.media.vo.MimeType
import cloud.luigi99.blog.media.domain.media.vo.OriginalFileName
import cloud.luigi99.blog.media.domain.media.vo.PublicUrl
import cloud.luigi99.blog.media.domain.media.vo.StorageKey
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import java.util.Optional

/**
 * MediaFileRepositoryAdapter 테스트
 *
 * JPA를 통한 미디어 파일 데이터 영속화 및 도메인 이벤트 발행을 검증합니다.
 */
class MediaFileRepositoryAdapterTest :
    BehaviorSpec({
        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        val jpaRepository = mockk<MediaFileJpaRepository>()
        val eventContextManager = mockk<EventContextManager>()
        val domainEventPublisher = mockk<DomainEventPublisher>()
        val adapter = MediaFileRepositoryAdapter(jpaRepository, eventContextManager, domainEventPublisher)

        Given("새로운 파일을 업로드할 때") {
            val mediaFile =
                MediaFile.upload(
                    originalFileName = OriginalFileName("test.png"),
                    mimeType = MimeType("image/png"),
                    fileSize = FileSize(1024L),
                    storageKey = StorageKey("2026/01/test-uuid.png"),
                    publicUrl = PublicUrl("https://cdn.example.com/2026/01/test-uuid.png"),
                )

            When("파일 정보를 저장하면") {
                val jpaEntity = MediaFileMapper.toEntity(mediaFile)
                val events =
                    listOf(
                        MediaFileUploadedEvent(
                            fileId = mediaFile.entityId,
                            originalFileName = mediaFile.originalFileName,
                            fileSize = mediaFile.fileSize,
                        ),
                    )

                every { jpaRepository.save(any()) } returns jpaEntity
                every { eventContextManager.getDomainEventsAndClear() } returns events
                justRun { domainEventPublisher.publish(any()) }

                val saved = adapter.save(mediaFile)

                Then("저장된 파일 정보가 반환되어야 한다") {
                    saved shouldNotBe null
                    saved.entityId shouldBe mediaFile.entityId
                    saved.originalFileName shouldBe mediaFile.originalFileName
                    saved.mimeType shouldBe mediaFile.mimeType
                    saved.fileSize shouldBe mediaFile.fileSize
                }

                Then("도메인 이벤트가 발행되어야 한다") {
                    verify(exactly = 1) {
                        domainEventPublisher.publish(match { it is MediaFileUploadedEvent })
                    }
                }
            }
        }

        Given("파일 ID가 주어졌을 때") {
            val fileId = MediaFileId.generate()

            When("해당 ID로 파일을 조회하면") {
                val jpaEntity =
                    MediaFileJpaEntity.from(
                        entityId = fileId.value,
                        originalFileName = "test.png",
                        mimeType = "image/png",
                        fileSize = 1024L,
                        storageKey = "2026/01/test-uuid.png",
                        publicUrl = "https://cdn.example.com/2026/01/test-uuid.png",
                    )

                every { jpaRepository.findById(fileId.value) } returns Optional.of(jpaEntity)

                val found = adapter.findById(fileId)

                Then("파일이 조회되어야 한다") {
                    found shouldNotBe null
                    found?.entityId shouldBe fileId
                    found?.originalFileName?.value shouldBe "test.png"
                }
            }

            When("존재하지 않는 ID로 조회하면") {
                every { jpaRepository.findById(fileId.value) } returns Optional.empty()

                val found = adapter.findById(fileId)

                Then("null이 반환되어야 한다") {
                    found shouldBe null
                }
            }
        }

        Given("StorageKey가 주어졌을 때") {
            val storageKey = StorageKey("2026/01/test-uuid.png")

            When("해당 키로 파일을 조회하면") {
                val jpaEntity =
                    MediaFileJpaEntity.from(
                        entityId = MediaFileId.generate().value,
                        originalFileName = "test.png",
                        mimeType = "image/png",
                        fileSize = 1024L,
                        storageKey = storageKey.value,
                        publicUrl = "https://cdn.example.com/2026/01/test-uuid.png",
                    )

                every { jpaRepository.findByStorageKeyValue(storageKey.value) } returns jpaEntity

                val found = adapter.findByStorageKey(storageKey)

                Then("파일이 조회되어야 한다") {
                    found shouldNotBe null
                    found?.storageKey shouldBe storageKey
                }
            }
        }

        Given("여러 파일이 저장되어 있을 때") {
            When("전체 파일 목록을 조회하면") {
                val entities =
                    listOf(
                        MediaFileJpaEntity.from(
                            entityId = MediaFileId.generate().value,
                            originalFileName = "test1.png",
                            mimeType = "image/png",
                            fileSize = 1024L,
                            storageKey = "2026/01/test1.png",
                            publicUrl = "https://cdn.example.com/2026/01/test1.png",
                        ),
                        MediaFileJpaEntity.from(
                            entityId = MediaFileId.generate().value,
                            originalFileName = "test2.jpg",
                            mimeType = "image/jpeg",
                            fileSize = 2048L,
                            storageKey = "2026/01/test2.jpg",
                            publicUrl = "https://cdn.example.com/2026/01/test2.jpg",
                        ),
                    )

                every { jpaRepository.findAll() } returns entities

                val files = adapter.findAll()

                Then("모든 파일이 조회되어야 한다") {
                    files shouldHaveSize 2
                    files[0].originalFileName.value shouldBe "test1.png"
                    files[1].originalFileName.value shouldBe "test2.jpg"
                }
            }
        }

        Given("삭제할 파일 ID가 주어졌을 때") {
            val fileId = MediaFileId.generate()

            When("파일을 삭제하면") {
                justRun { jpaRepository.deleteById(fileId.value) }

                adapter.deleteById(fileId)

                Then("Repository의 deleteById가 호출되어야 한다") {
                    verify(exactly = 1) { jpaRepository.deleteById(fileId.value) }
                }
            }
        }
    })
