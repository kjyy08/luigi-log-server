package cloud.luigi99.blog.media.domain.media.event

import cloud.luigi99.blog.common.domain.event.DomainEvent
import cloud.luigi99.blog.media.domain.media.vo.FileSize
import cloud.luigi99.blog.media.domain.media.vo.MediaFileId
import cloud.luigi99.blog.media.domain.media.vo.OriginalFileName
import java.time.LocalDateTime

/**
 * 미디어 파일 업로드 이벤트
 *
 * 파일이 성공적으로 업로드되었을 때 발행됩니다.
 */
data class MediaFileUploadedEvent(
    val fileId: MediaFileId,
    val originalFileName: OriginalFileName,
    val fileSize: FileSize,
    val occurredAt: LocalDateTime = LocalDateTime.now(),
) : DomainEvent
