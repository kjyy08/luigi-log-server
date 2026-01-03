package cloud.luigi99.blog.media.adapter.`in`.web

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.media.adapter.`in`.web.dto.FileListResponse
import cloud.luigi99.blog.media.adapter.`in`.web.dto.FileResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Media", description = "미디어 파일 관리 API")
interface MediaApi {
    @Operation(
        summary = "파일 업로드",
        description = "이미지 등 파일을 업로드합니다. 최대 10MB까지 업로드 가능합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "파일 업로드 성공",
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 파일 형식 또는 크기 초과",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "InvalidFileType",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "MEDIA_002",
                                    "message": "지원하지 않는 파일 형식입니다."
                                  },
                                  "timestamp": "2026-01-03T10:00:00Z"
                                }
                                """,
                            ),
                            ExampleObject(
                                name = "FileSizeExceeded",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "MEDIA_003",
                                    "message": "파일 크기가 제한을 초과했습니다."
                                  },
                                  "timestamp": "2026-01-03T10:00:00Z"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "500",
                description = "업로드 실패 (서버 오류)",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "UploadFailed",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "MEDIA_001",
                                    "message": "파일 업로드에 실패했습니다."
                                  },
                                  "timestamp": "2026-01-03T10:00:00Z"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun uploadFile(
        @Parameter(
            description = "업로드할 파일",
            required = true,
            content = [Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)],
        )
        @RequestPart
        file: MultipartFile,
    ): ResponseEntity<CommonResponse<FileResponse>>

    @Operation(
        summary = "파일 상세 조회",
        description = "파일 ID를 통해 파일의 메타데이터와 Public URL을 조회합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "파일 조회 성공",
            ),
            ApiResponse(
                responseCode = "404",
                description = "파일을 찾을 수 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "FileNotFound",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "MEDIA_004",
                                    "message": "파일을 찾을 수 없습니다."
                                  },
                                  "timestamp": "2026-01-03T10:00:00Z"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun getFile(
        @Parameter(description = "파일 ID", required = true)
        fileId: String,
    ): ResponseEntity<CommonResponse<FileResponse>>

    @Operation(
        summary = "파일 목록 조회",
        description = "업로드된 모든 파일의 목록을 조회합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "파일 목록 조회 성공",
            ),
        ],
    )
    fun getFileList(): ResponseEntity<CommonResponse<FileListResponse>>

    @Operation(
        summary = "파일 삭제",
        description = "파일 ID에 해당하는 파일을 삭제합니다. Storage와 DB에서 모두 제거됩니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "파일 삭제 성공",
                content = [Content(schema = Schema(hidden = true))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "파일을 찾을 수 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "FileNotFound",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "MEDIA_004",
                                    "message": "파일을 찾을 수 없습니다."
                                  },
                                  "timestamp": "2026-01-03T10:00:00Z"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun deleteFile(
        @Parameter(description = "파일 ID", required = true)
        fileId: String,
    ): ResponseEntity<Unit>
}
