package cloud.luigi99.blog.auth.credentials.adapter.`in`.web.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.redirect")
data class OAuth2Properties(val baseUrl: String, val endPoint: EndPoint) {
    data class EndPoint(val success: String, val error: String)
}
