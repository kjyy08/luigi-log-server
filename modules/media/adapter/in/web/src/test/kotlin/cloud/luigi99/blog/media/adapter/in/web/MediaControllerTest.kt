package cloud.luigi99.blog.media.adapter.`in`.web

import cloud.luigi99.blog.media.application.media.port.`in`.command.DeleteFileUseCase
import cloud.luigi99.blog.media.application.media.port.`in`.command.MediaCommandFacade
import cloud.luigi99.blog.media.application.media.port.`in`.command.UploadFileUseCase
import cloud.luigi99.blog.media.application.media.port.`in`.query.GetFileListUseCase
import cloud.luigi99.blog.media.application.media.port.`in`.query.GetFileUseCase
import cloud.luigi99.blog.media.application.media.port.`in`.query.MediaQueryFacade
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import java.time.LocalDateTime

/**
 * MediaController 테스트
 *
 * 파일 업로드/조회/삭제 API 엔드포인트를 검증합니다.
 */
class MediaControllerTest :
    BehaviorSpec({
        val mediaCommandFacade = mockk<MediaCommandFacade>()
        val mediaQueryFacade = mockk<MediaQueryFacade>()
        val uploadFileUseCase = mockk<UploadFileUseCase>()
        val getFileUseCase = mockk<GetFileUseCase>()
        val getFileListUseCase = mockk<GetFileListUseCase>()
        val deleteFileUseCase = mockk<DeleteFileUseCase>()

        val controller = MediaController(mediaCommandFacade, mediaQueryFacade)

        Given("파일 업로드 요청이 주어졌을 때") {
            val file =
                MockMultipartFile(
                    "file",
                    "test.png",
                    "image/png",
                    "test content".toByteArray(),
                )

            val uploadResponse =
                UploadFileUseCase.Response(
                    fileId = "file-123",
                    originalFileName = "test.png",
                    mimeType = "image/png",
                    fileSize = 12L,
                    storageKey = "2026/01/test.png",
                    publicUrl = "https://cdn.example.com/2026/01/test.png",
                )

            When("파일을 업로드하면") {
                every { mediaCommandFacade.uploadFile() } returns uploadFileUseCase
                every { uploadFileUseCase.execute(any()) } returns uploadResponse

                val result = controller.uploadFile(file)

                Then("201 Created 응답이 반환되어야 한다") {
                    result.statusCode shouldBe HttpStatus.CREATED
                }

                Then("UploadFileUseCase가 호출되어야 한다") {
                    verify(exactly = 1) {
                        uploadFileUseCase.execute(
                            match {
                                it.originalFileName == "test.png" &&
                                    it.mimeType == "image/png" &&
                                    it.fileSize == 12L
                            },
                        )
                    }
                }
            }
        }

        Given("파일 ID가 주어졌을 때") {
            val fileId = "file-123"
            val fileResponse =
                GetFileUseCase.Response(
                    fileId = fileId,
                    originalFileName = "test.png",
                    mimeType = "image/png",
                    fileSize = 1024L,
                    storageKey = "2026/01/test.png",
                    publicUrl = "https://cdn.example.com/2026/01/test.png",
                    createdAt = LocalDateTime.now(),
                )

            When("파일 정보를 조회하면") {
                every { mediaQueryFacade.getFile() } returns getFileUseCase
                every { getFileUseCase.execute(any()) } returns fileResponse

                val result = controller.getFile(fileId)

                Then("200 OK 응답이 반환되어야 한다") {
                    result.statusCode shouldBe HttpStatus.OK
                }

                Then("GetFileUseCase가 호출되어야 한다") {
                    verify(exactly = 1) {
                        getFileUseCase.execute(match { it.fileId == fileId })
                    }
                }
            }
        }

        Given("파일 목록 조회 요청이 주어졌을 때") {
            val fileListResponse =
                GetFileListUseCase.Response(
                    files =
                        listOf(
                            GetFileListUseCase.FileSummary(
                                fileId = "file-1",
                                originalFileName = "test1.png",
                                mimeType = "image/png",
                                fileSize = 1024L,
                                publicUrl = "https://cdn.example.com/test1.png",
                                createdAt = LocalDateTime.now(),
                            ),
                        ),
                )

            When("파일 목록을 조회하면") {
                every { mediaQueryFacade.getFileList() } returns getFileListUseCase
                every { getFileListUseCase.execute(any()) } returns fileListResponse

                val result = controller.getFileList()

                Then("200 OK 응답이 반환되어야 한다") {
                    result.statusCode shouldBe HttpStatus.OK
                }

                Then("GetFileListUseCase가 호출되어야 한다") {
                    verify(exactly = 1) {
                        getFileListUseCase.execute(any())
                    }
                }
            }
        }

        Given("삭제할 파일 ID가 주어졌을 때") {
            val fileId = "file-123"

            When("파일을 삭제하면") {
                every { mediaCommandFacade.deleteFile() } returns deleteFileUseCase
                every { deleteFileUseCase.execute(any()) } returns Unit

                val result = controller.deleteFile(fileId)

                Then("204 No Content 응답이 반환되어야 한다") {
                    result.statusCode shouldBe HttpStatus.NO_CONTENT
                }

                Then("DeleteFileUseCase가 호출되어야 한다") {
                    verify(exactly = 1) {
                        deleteFileUseCase.execute(match { it.fileId == fileId })
                    }
                }
            }
        }
    })
