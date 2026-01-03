package cloud.luigi99.blog.media.adapter.out.storage.r2.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

/**
 * Cloudflare R2 Storage 설정
 *
 * R2는 AWS S3 호환 API를 제공하므로 AWS SDK S3Client를 사용합니다.
 */
@Configuration
@EnableConfigurationProperties(R2Properties::class)
class R2Config(private val properties: R2Properties) {
    /**
     * R2용 S3Client Bean을 생성합니다.
     *
     * @return S3Client R2에 연결된 S3 클라이언트
     */
    @Bean
    fun s3Client(): S3Client =
        S3Client
            .builder()
            .region(Region.US_EAST_1)
            .endpointOverride(URI.create("https://${properties.accountId}.r2.cloudflarestorage.com"))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(properties.accessKeyId, properties.secretAccessKey),
                ),
            ).build()

    @Bean
    fun bucketName(): String = properties.bucketName

    @Bean
    fun publicUrl(): String = properties.publicUrl
}
