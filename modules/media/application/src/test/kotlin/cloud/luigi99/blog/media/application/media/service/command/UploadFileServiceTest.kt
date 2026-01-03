package cloud.luigi99.blog.media.application.media.service.command

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.media.application.media.port.`in`.command.UploadFileUseCase
import cloud.luigi99.blog.media.application.media.port.out.MediaFileRepository
import cloud.luigi99.blog.media.application.media.port.out.StoragePort
import cloud.luigi99.blog.media.domain.media.model.MediaFile
import cloud.luigi99.blog.media.domain.media.vo.FileSize
import cloud.luigi99.blog.media.domain.media.vo.MediaFileId
import cloud.luigi99.blog.media.domain.media.vo.MimeType
import cloud.luigi99.blog.media.domain.media.vo.OriginalFileName
import cloud.luigi99.blog.media.domain.media.vo.PublicUrl
import cloud.luigi99.blog.media.domain.media.vo.StorageKey
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import java.util.UUID

class UploadFileServiceTest :
    BehaviorSpec({
        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }
        Given("유효한 이미지 파일이 주어졌을 때") {
            val mediaFileRepository = mockk<MediaFileRepository>()
            val storagePort = mockk<StoragePort>()
            val service = UploadFileService(mediaFileRepository, storagePort)

            val command =
                UploadFileUseCase.Command(
                    originalFileName = "test.png",
                    mimeType = "image/png",
                    fileSize = 1024L,
                    fileData = ByteArray(1024),
                )

            When("파일을 업로드하면") {
                val savedFile =
                    MediaFile.from(
                        entityId = MediaFileId(UUID.randomUUID()),
                        originalFileName = OriginalFileName("test.png"),
                        mimeType = MimeType("image/png"),
                        fileSize = FileSize(1024L),
                        storageKey = StorageKey("2026/01/test-uuid.png"),
                        publicUrl = PublicUrl("https://cdn.example.com/2026/01/test-uuid.png"),
                        createdAt = null,
                        updatedAt = null,
                    )

                every { storagePort.upload(any(), any(), any()) } returns Unit
                every { storagePort.getPublicUrl(any()) } returns
                    PublicUrl("https://cdn.example.com/2026/01/test-uuid.png")
                every { mediaFileRepository.save(any()) } returns savedFile

                val response = service.execute(command)

                Then("Storage에 업로드가 호출된다") {
                    verify(exactly = 1) { storagePort.upload(any(), any(), "image/png") }
                }

                Then("Repository의 save가 호출된다") {
                    verify(exactly = 1) { mediaFileRepository.save(any()) }
                }

                Then("업로드 결과가 반환된다") {
                    response.fileId shouldNotBe null
                    response.originalFileName shouldBe "test.png"
                    response.mimeType shouldBe "image/png"
                    response.fileSize shouldBe 1024L
                    response.publicUrl shouldBe "https://cdn.example.com/2026/01/test-uuid.png"
                }
            }
        }

        Given("허용되지 않은 파일 타입이 주어졌을 때") {
            val mediaFileRepository = mockk<MediaFileRepository>()
            val storagePort = mockk<StoragePort>()
            val service = UploadFileService(mediaFileRepository, storagePort)

            val command =
                UploadFileUseCase.Command(
                    originalFileName = "test.exe",
                    mimeType = "application/x-msdownload",
                    fileSize = 1024L,
                    fileData = ByteArray(1024),
                )

            When("파일을 업로드하려고 하면") {
                Then("IllegalArgumentException이 발생한다") {
                    val exception =
                        shouldThrow<IllegalArgumentException> {
                            service.execute(command)
                        }
                    exception.message shouldContain "Unsupported MIME type"
                }
            }
        }

        Given("파일 크기가 0인 파일이 주어졌을 때") {
            val mediaFileRepository = mockk<MediaFileRepository>()
            val storagePort = mockk<StoragePort>()
            val service = UploadFileService(mediaFileRepository, storagePort)

            val command =
                UploadFileUseCase.Command(
                    originalFileName = "empty.png",
                    mimeType = "image/png",
                    fileSize = 0L,
                    fileData = ByteArray(0),
                )

            When("파일을 업로드하려고 하면") {
                Then("IllegalArgumentException이 발생한다") {
                    val exception =
                        shouldThrow<IllegalArgumentException> {
                            service.execute(command)
                        }
                    exception.message shouldContain "File size must be positive"
                }
            }
        }
    })
