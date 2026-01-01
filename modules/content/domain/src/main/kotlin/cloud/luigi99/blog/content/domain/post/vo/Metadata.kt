package cloud.luigi99.blog.content.domain.post.vo

import cloud.luigi99.blog.common.domain.ValueObject
import java.time.LocalDateTime

/**
 * Post의 시간 관련 메타데이터
 *
 * Post의 생성, 수정, 발행 시간을 관리합니다.
 *
 * @property createdAt 생성 시간
 * @property updatedAt 최종 수정 시간
 * @property publishedAt 발행 시간 (초안 상태에서는 null)
 */
data class Metadata(
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val publishedAt: LocalDateTime? = null,
) : ValueObject
