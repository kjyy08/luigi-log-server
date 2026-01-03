package cloud.luigi99.blog.media.domain.media.model

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.media.domain.media.vo.*
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import java.time.LocalDateTime

/**
 * MediaFile 애그리거트 도메인 로직 테스트
 *
 * 이 테스트는 순수 도메인 로직만 검증합니다.
 * 도메인 이벤트 발행은 Application 레이어의 통합 테스트에서 검증합니다.
 */
class MediaFileTest :
    BehaviorSpec({
        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("업로드할 미디어 파일의 정보(이름, 크기, 타입 등)가 유효한 경우") {
            val originalFileName = OriginalFileName("test.png")
            val mimeType = MimeType("image/png")
            val fileSize = FileSize(1024 * 1024) // 1MB
            val storageKey = StorageKey("2025/01/test.png")
            val publicUrl = PublicUrl("https://r2.dev/2025/01/test.png")

            When("미디어 파일 업로드를 수행하면") {
                val mediaFile =
                    MediaFile.upload(
                        originalFileName = originalFileName,
                        mimeType = mimeType,
                        fileSize = fileSize,
                        storageKey = storageKey,
                        publicUrl = publicUrl,
                    )

                Then("새로운 미디어 파일 식별자가 부여된다") {
                    mediaFile.entityId shouldNotBe null
                }

                Then("파일의 메타데이터(크기, 경로, URL)가 도메인 객체에 정확히 기록된다") {
                    mediaFile.originalFileName shouldBe originalFileName
                    mediaFile.mimeType shouldBe mimeType
                    mediaFile.fileSize shouldBe fileSize
                    mediaFile.storageKey shouldBe storageKey
                    mediaFile.publicUrl shouldBe publicUrl
                }
            }
        }

        Given("이미 저장된 미디어 파일의 데이터가 존재할 때") {
            val entityId = MediaFileId.generate()
            val originalFileName = OriginalFileName("test.png")
            val mimeType = MimeType("image/png")
            val fileSize = FileSize(1024 * 1024)
            val storageKey = StorageKey("2025/01/test.png")
            val publicUrl = PublicUrl("https://r2.dev/2025/01/test.png")
            val createdAt = LocalDateTime.now().minusDays(1)
            val updatedAt = LocalDateTime.now()

            When("저장된 데이터를 기반으로 도메인 객체를 복원하면") {
                val mediaFile =
                    MediaFile.from(
                        entityId = entityId,
                        originalFileName = originalFileName,
                        mimeType = mimeType,
                        fileSize = fileSize,
                        storageKey = storageKey,
                        publicUrl = publicUrl,
                        createdAt = createdAt,
                        updatedAt = updatedAt,
                    )

                Then("데이터 무결성이 유지된 상태로 객체가 생성된다") {
                    mediaFile.entityId shouldBe entityId
                    mediaFile.originalFileName shouldBe originalFileName
                    mediaFile.mimeType shouldBe mimeType
                    mediaFile.fileSize shouldBe fileSize
                    mediaFile.storageKey shouldBe storageKey
                    mediaFile.publicUrl shouldBe publicUrl
                    mediaFile.createdAt shouldBe createdAt
                    mediaFile.updatedAt shouldBe updatedAt
                }
            }
        }

        Given("이미 등록된 미디어 파일이 존재하는 경우") {
            val mediaFile =
                MediaFile.upload(
                    originalFileName = OriginalFileName("test.png"),
                    mimeType = MimeType("image/png"),
                    fileSize = FileSize(1024 * 1024),
                    storageKey = StorageKey("2025/01/test.png"),
                    publicUrl = PublicUrl("https://r2.dev/2025/01/test.png"),
                )

            When("해당 미디어 파일 삭제를 요청하면") {
                mediaFile.delete()

                Then("삭제 처리 후에도 파일의 기본 정보(ID, 이름)는 확인 가능해야 한다") {
                    mediaFile.entityId shouldNotBe null
                    mediaFile.originalFileName.value shouldBe "test.png"
                }
            }
        }

        Given("연속으로 여러 파일을 업로드하는 경우") {
            When("각각 별도의 업로드를 수행하면") {
                val file1 =
                    MediaFile.upload(
                        originalFileName = OriginalFileName("test1.png"),
                        mimeType = MimeType("image/png"),
                        fileSize = FileSize(1024),
                        storageKey = StorageKey("2025/01/test1.png"),
                        publicUrl = PublicUrl("https://r2.dev/2025/01/test1.png"),
                    )

                val file2 =
                    MediaFile.upload(
                        originalFileName = OriginalFileName("test2.png"),
                        mimeType = MimeType("image/png"),
                        fileSize = FileSize(1024),
                        storageKey = StorageKey("2025/01/test2.png"),
                        publicUrl = PublicUrl("https://r2.dev/2025/01/test2.png"),
                    )

                Then("각 파일은 서로 다른 고유 식별자를 가져야 한다") {
                    file1.entityId shouldNotBe file2.entityId
                }
            }
        }
    })
