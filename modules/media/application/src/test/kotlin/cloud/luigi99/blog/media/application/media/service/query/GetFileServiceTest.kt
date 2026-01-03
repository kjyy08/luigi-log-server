package cloud.luigi99.blog.media.application.media.service.query

import cloud.luigi99.blog.media.application.media.port.`in`.query.GetFileUseCase
import cloud.luigi99.blog.media.application.media.port.out.MediaFileRepository
import cloud.luigi99.blog.media.domain.media.exception.FileNotFoundException
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
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class GetFileServiceTest :
    BehaviorSpec({
        Given("파일 ID가 주어졌을 때") {
            val mediaFileRepository = mockk<MediaFileRepository>()
            val service = GetFileService(mediaFileRepository)

            val fileId = MediaFileId.generate()
            val query = GetFileUseCase.Query(fileId.value.toString())

            When("해당 ID의 파일이 존재하면") {
                val existingFile =
                    MediaFile.from(
                        entityId = fileId,
                        originalFileName = OriginalFileName("test.png"),
                        mimeType = MimeType("image/png"),
                        fileSize = FileSize(1024L),
                        storageKey = StorageKey("2026/01/test-uuid.png"),
                        publicUrl = PublicUrl("https://cdn.example.com/2026/01/test-uuid.png"),
                        createdAt = LocalDateTime.of(2026, 1, 3, 10, 0),
                        updatedAt = null,
                    )

                every { mediaFileRepository.findById(fileId) } returns existingFile

                val response = service.execute(query)

                Then("Repository의 findById가 호출된다") {
                    verify(exactly = 1) { mediaFileRepository.findById(fileId) }
                }

                Then("파일 정보가 반환된다") {
                    response.fileId shouldBe fileId.value.toString()
                    response.originalFileName shouldBe "test.png"
                    response.mimeType shouldBe "image/png"
                    response.fileSize shouldBe 1024L
                    response.storageKey shouldBe "2026/01/test-uuid.png"
                    response.publicUrl shouldBe "https://cdn.example.com/2026/01/test-uuid.png"
                    response.createdAt shouldBe LocalDateTime.of(2026, 1, 3, 10, 0)
                }
            }

            When("해당 ID의 파일이 존재하지 않으면") {
                every { mediaFileRepository.findById(fileId) } returns null

                Then("FileNotFoundException이 발생한다") {
                    val exception =
                        shouldThrow<FileNotFoundException> {
                            service.execute(query)
                        }
                    exception.message shouldBe "파일을 찾을 수 없습니다: ${fileId.value}"
                }
            }
        }
    })
