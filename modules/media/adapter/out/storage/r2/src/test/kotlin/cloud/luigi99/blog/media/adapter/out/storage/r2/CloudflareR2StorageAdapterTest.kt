package cloud.luigi99.blog.media.adapter.out.storage.r2

import cloud.luigi99.blog.media.domain.media.exception.FileUploadFailedException
import cloud.luigi99.blog.media.domain.media.vo.PublicUrl
import cloud.luigi99.blog.media.domain.media.vo.StorageKey
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse

/**
 * CloudflareR2StorageAdapter 테스트
 *
 * S3Client Mock을 사용하여 파일 업로드/삭제/URL 생성 기능을 검증합니다.
 */
class CloudflareR2StorageAdapterTest :
    BehaviorSpec({
        val s3Client = mockk<S3Client>()
        val bucketName = "test-bucket"
        val publicUrl = "https://cdn.example.com"
        val adapter = CloudflareR2StorageAdapter(s3Client, bucketName, publicUrl)

        Given("파일 데이터가 주어졌을 때") {
            val storageKey = StorageKey("2026/01/test-uuid.png")
            val fileData = "test file content".toByteArray()
            val contentType = "image/png"

            When("파일을 업로드하면") {
                every {
                    s3Client.putObject(any<PutObjectRequest>(), any<RequestBody>())
                } returns PutObjectResponse.builder().build()

                adapter.upload(storageKey, fileData, contentType)

                Then("S3Client의 putObject가 호출되어야 한다") {
                    verify(exactly = 1) {
                        s3Client.putObject(
                            match<PutObjectRequest> {
                                it.bucket() == bucketName &&
                                    it.key() == storageKey.value &&
                                    it.contentType() == contentType &&
                                    it.contentLength() == fileData.size.toLong()
                            },
                            any<RequestBody>(),
                        )
                    }
                }
            }

            When("업로드 중 예외가 발생하면") {
                every {
                    s3Client.putObject(any<PutObjectRequest>(), any<RequestBody>())
                } throws RuntimeException("S3 upload failed")

                val exception =
                    shouldThrow<FileUploadFailedException> {
                        adapter.upload(storageKey, fileData, contentType)
                    }

                Then("FileUploadFailedException이 발생해야 한다") {
                    exception.message shouldContain "파일 업로드에 실패했습니다"
                }
            }
        }

        Given("StorageKey가 주어졌을 때") {
            val storageKey = StorageKey("2026/01/test-uuid.png")

            When("파일을 삭제하면") {
                every {
                    s3Client.deleteObject(any<DeleteObjectRequest>())
                } returns DeleteObjectResponse.builder().build()

                adapter.delete(storageKey)

                Then("S3Client의 deleteObject가 호출되어야 한다") {
                    verify(exactly = 1) {
                        s3Client.deleteObject(
                            match<DeleteObjectRequest> {
                                it.bucket() == bucketName &&
                                    it.key() == storageKey.value
                            },
                        )
                    }
                }
            }

            When("삭제 중 예외가 발생해도") {
                every {
                    s3Client.deleteObject(any<DeleteObjectRequest>())
                } throws RuntimeException("S3 delete failed")

                Then("예외가 발생하지 않아야 한다") {
                    adapter.delete(storageKey)
                }
            }

            When("Public URL을 생성하면") {
                val publicUrlResult = adapter.getPublicUrl(storageKey)

                Then("올바른 URL이 반환되어야 한다") {
                    publicUrlResult shouldBe PublicUrl("$publicUrl/${storageKey.value}")
                }
            }
        }
    })
