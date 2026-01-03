package cloud.luigi99.blog.media.adapter.out.storage.r2.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Cloudflare R2 설정 프로퍼티
 *
 * application.yml의 cloudflare.r2 설정을 바인딩합니다.
 */
@ConfigurationProperties(prefix = "cloudflare.r2")
data class R2Properties(
    val accountId: String,
    val accessKeyId: String,
    val secretAccessKey: String,
    val bucketName: String,
    val publicUrl: String,
)
