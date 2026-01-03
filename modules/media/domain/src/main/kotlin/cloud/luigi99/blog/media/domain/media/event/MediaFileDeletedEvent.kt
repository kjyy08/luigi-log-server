package cloud.luigi99.blog.media.domain.media.event

import cloud.luigi99.blog.common.domain.event.DomainEvent
import cloud.luigi99.blog.media.domain.media.vo.MediaFileId
import cloud.luigi99.blog.media.domain.media.vo.StorageKey
import java.time.LocalDateTime

/**
 * 미디어 파일 삭제 이벤트
 *
 * 파일이 삭제되었을 때 발행됩니다.
 */
data class MediaFileDeletedEvent(
    val fileId: MediaFileId,
    val storageKey: StorageKey,
    val occurredAt: LocalDateTime = LocalDateTime.now(),
) : DomainEvent
