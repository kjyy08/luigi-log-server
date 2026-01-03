package cloud.luigi99.blog.media.adapter.`in`.web.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 파일 응답 DTO
 */
data class FileResponse(
    @field:Schema(description = "파일 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val fileId: String,
    @field:Schema(description = "원본 파일명", example = "image.png")
    val originalFileName: String,
    @field:Schema(description = "MIME 타입", example = "image/png")
    val mimeType: String,
    @field:Schema(description = "파일 크기 (bytes)", example = "1024")
    val fileSize: Long,
    @field:Schema(description = "Public URL", example = "https://cdn.example.com/2026/01/image.png")
    val publicUrl: String,
)
