package cloud.luigi99.blog.media.adapter.out.persistence.jpa.media

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.media.domain.media.model.MediaFile
import cloud.luigi99.blog.media.domain.media.vo.FileSize
import cloud.luigi99.blog.media.domain.media.vo.MediaFileId
import cloud.luigi99.blog.media.domain.media.vo.MimeType
import cloud.luigi99.blog.media.domain.media.vo.OriginalFileName
import cloud.luigi99.blog.media.domain.media.vo.PublicUrl
import cloud.luigi99.blog.media.domain.media.vo.StorageKey
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import java.time.LocalDateTime

/**
 * MediaFileMapper 테스트
 *
 * Domain 모델과 JPA Entity 간의 변환을 검증합니다.
 */
class MediaFileMapperTest :
    BehaviorSpec({
        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }
        Given("Domain 모델이 주어졌을 때") {
            val now = LocalDateTime.now()
            val mediaFile =
                MediaFile.from(
                    entityId = MediaFileId.generate(),
                    originalFileName = OriginalFileName("test.png"),
                    mimeType = MimeType("image/png"),
                    fileSize = FileSize(1024L),
                    storageKey = StorageKey("2026/01/test-uuid.png"),
                    publicUrl = PublicUrl("https://cdn.example.com/2026/01/test-uuid.png"),
                    createdAt = now,
                    updatedAt = now,
                )

            When("JPA Entity로 변환하면") {
                val jpaEntity = MediaFileMapper.toEntity(mediaFile)

                Then("모든 필드가 올바르게 매핑되어야 한다") {
                    jpaEntity.id shouldBe mediaFile.entityId.value
                    jpaEntity.originalFileName shouldBe mediaFile.originalFileName.value
                    jpaEntity.mimeType shouldBe mediaFile.mimeType.value
                    jpaEntity.fileSize shouldBe mediaFile.fileSize.bytes
                    jpaEntity.storageKey shouldBe mediaFile.storageKey.value
                    jpaEntity.publicUrl shouldBe mediaFile.publicUrl.value
                    jpaEntity.createdAt shouldBe now
                    jpaEntity.updatedAt shouldBe now
                }
            }
        }

        Given("JPA Entity가 주어졌을 때") {
            val now = LocalDateTime.now()
            val fileId = MediaFileId.generate()
            val jpaEntity =
                MediaFileJpaEntity
                    .from(
                        entityId = fileId.value,
                        originalFileName = "test.png",
                        mimeType = "image/png",
                        fileSize = 1024L,
                        storageKey = "2026/01/test-uuid.png",
                        publicUrl = "https://cdn.example.com/2026/01/test-uuid.png",
                    ).apply {
                        createdAt = now
                        updatedAt = now
                    }

            When("Domain 모델로 변환하면") {
                val mediaFile = MediaFileMapper.toDomain(jpaEntity)

                Then("모든 필드가 올바르게 매핑되어야 한다") {
                    mediaFile.entityId.value shouldBe jpaEntity.id
                    mediaFile.originalFileName.value shouldBe jpaEntity.originalFileName
                    mediaFile.mimeType.value shouldBe jpaEntity.mimeType
                    mediaFile.fileSize.bytes shouldBe jpaEntity.fileSize
                    mediaFile.storageKey.value shouldBe jpaEntity.storageKey
                    mediaFile.publicUrl.value shouldBe jpaEntity.publicUrl
                    mediaFile.createdAt shouldBe now
                    mediaFile.updatedAt shouldBe now
                }
            }
        }

        Given("Domain 모델과 JPA Entity가 주어졌을 때") {
            val mediaFile =
                MediaFile.upload(
                    originalFileName = OriginalFileName("test.png"),
                    mimeType = MimeType("image/png"),
                    fileSize = FileSize(1024L),
                    storageKey = StorageKey("2026/01/test-uuid.png"),
                    publicUrl = PublicUrl("https://cdn.example.com/2026/01/test-uuid.png"),
                )

            When("Domain → JPA → Domain 변환을 수행하면") {
                val jpaEntity = MediaFileMapper.toEntity(mediaFile)
                val converted = MediaFileMapper.toDomain(jpaEntity)

                Then("원본과 동일한 값을 가져야 한다") {
                    converted.entityId shouldBe mediaFile.entityId
                    converted.originalFileName shouldBe mediaFile.originalFileName
                    converted.mimeType shouldBe mediaFile.mimeType
                    converted.fileSize shouldBe mediaFile.fileSize
                    converted.storageKey shouldBe mediaFile.storageKey
                    converted.publicUrl shouldBe mediaFile.publicUrl
                }
            }
        }
    })
