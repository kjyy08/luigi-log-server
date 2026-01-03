package cloud.luigi99.blog.media.application.media.service.query

import cloud.luigi99.blog.media.application.media.port.`in`.query.GetFileListUseCase
import cloud.luigi99.blog.media.application.media.port.out.MediaFileRepository
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
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class GetFileListServiceTest :
    BehaviorSpec({
        Given("파일 목록 조회 서비스가 주어졌을 때") {
            val mediaFileRepository = mockk<MediaFileRepository>()
            val service = GetFileListService(mediaFileRepository)

            val query = GetFileListUseCase.Query()

            When("파일이 여러 개 존재하면") {
                val files =
                    listOf(
                        MediaFile.from(
                            entityId = MediaFileId.generate(),
                            originalFileName = OriginalFileName("test1.png"),
                            mimeType = MimeType("image/png"),
                            fileSize = FileSize(1024L),
                            storageKey = StorageKey("2026/01/test1.png"),
                            publicUrl = PublicUrl("https://cdn.example.com/2026/01/test1.png"),
                            createdAt = LocalDateTime.of(2026, 1, 3, 10, 0),
                            updatedAt = null,
                        ),
                        MediaFile.from(
                            entityId = MediaFileId.generate(),
                            originalFileName = OriginalFileName("test2.jpg"),
                            mimeType = MimeType("image/jpeg"),
                            fileSize = FileSize(2048L),
                            storageKey = StorageKey("2026/01/test2.jpg"),
                            publicUrl = PublicUrl("https://cdn.example.com/2026/01/test2.jpg"),
                            createdAt = LocalDateTime.of(2026, 1, 3, 11, 0),
                            updatedAt = null,
                        ),
                    )

                every { mediaFileRepository.findAll() } returns files

                val response = service.execute(query)

                Then("Repository의 findAll이 호출된다") {
                    verify(exactly = 1) { mediaFileRepository.findAll() }
                }

                Then("모든 파일 정보가 반환된다") {
                    response.files shouldHaveSize 2
                    response.files[0].originalFileName shouldBe "test1.png"
                    response.files[1].originalFileName shouldBe "test2.jpg"
                }
            }

            When("파일이 하나도 없으면") {
                every { mediaFileRepository.findAll() } returns emptyList()

                val response = service.execute(query)

                Then("빈 목록이 반환된다") {
                    response.files shouldHaveSize 0
                }
            }
        }
    })
