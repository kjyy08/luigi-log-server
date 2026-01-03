package cloud.luigi99.blog.media.adapter.`in`.web

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.media.adapter.`in`.web.dto.FileListResponse
import cloud.luigi99.blog.media.adapter.`in`.web.dto.FileResponse
import cloud.luigi99.blog.media.application.media.port.`in`.command.DeleteFileUseCase
import cloud.luigi99.blog.media.application.media.port.`in`.command.MediaCommandFacade
import cloud.luigi99.blog.media.application.media.port.`in`.command.UploadFileUseCase
import cloud.luigi99.blog.media.application.media.port.`in`.query.GetFileListUseCase
import cloud.luigi99.blog.media.application.media.port.`in`.query.GetFileUseCase
import cloud.luigi99.blog.media.application.media.port.`in`.query.MediaQueryFacade
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

private val log = KotlinLogging.logger {}

/**
 * 미디어 파일 컨트롤러
 *
 * 파일 업로드/조회/삭제 등 미디어 파일 관련 요청을 처리합니다.
 */
@RestController
@RequestMapping("/api/v1/files")
class MediaController(
    private val mediaCommandFacade: MediaCommandFacade,
    private val mediaQueryFacade: MediaQueryFacade,
) : MediaApi {
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    override fun uploadFile(
        @RequestPart("file") file: MultipartFile,
    ): ResponseEntity<CommonResponse<FileResponse>> {
        log.info { "Uploading file: ${file.originalFilename}" }

        val response =
            mediaCommandFacade.uploadFile().execute(
                UploadFileUseCase.Command(
                    originalFileName = file.originalFilename ?: "unknown",
                    mimeType = file.contentType ?: "application/octet-stream",
                    fileSize = file.size,
                    fileData = file.bytes,
                ),
            )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                CommonResponse.success(
                    FileResponse(
                        fileId = response.fileId,
                        originalFileName = response.originalFileName,
                        mimeType = response.mimeType,
                        fileSize = response.fileSize,
                        publicUrl = response.publicUrl,
                    ),
                ),
            )
    }

    @GetMapping("/{fileId}")
    override fun getFile(
        @PathVariable fileId: String,
    ): ResponseEntity<CommonResponse<FileResponse>> {
        log.info { "Getting file: $fileId" }

        val response =
            mediaQueryFacade.getFile().execute(
                GetFileUseCase.Query(fileId = fileId),
            )

        return ResponseEntity.ok(
            CommonResponse.success(
                FileResponse(
                    fileId = response.fileId,
                    originalFileName = response.originalFileName,
                    mimeType = response.mimeType,
                    fileSize = response.fileSize,
                    publicUrl = response.publicUrl,
                ),
            ),
        )
    }

    @GetMapping
    override fun getFileList(): ResponseEntity<CommonResponse<FileListResponse>> {
        log.info { "Getting file list" }

        val response =
            mediaQueryFacade.getFileList().execute(
                GetFileListUseCase.Query(),
            )

        return ResponseEntity.ok(
            CommonResponse.success(
                FileListResponse(
                    files =
                        response.files.map {
                            FileListResponse.FileSummary(
                                fileId = it.fileId,
                                originalFileName = it.originalFileName,
                                mimeType = it.mimeType,
                                fileSize = it.fileSize,
                                publicUrl = it.publicUrl,
                            )
                        },
                ),
            ),
        )
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{fileId}")
    override fun deleteFile(
        @PathVariable fileId: String,
    ): ResponseEntity<Unit> {
        log.info { "Deleting file: $fileId" }

        mediaCommandFacade.deleteFile().execute(
            DeleteFileUseCase.Command(fileId = fileId),
        )

        return ResponseEntity.noContent().build()
    }
}
