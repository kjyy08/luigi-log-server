package cloud.luigi99.blog.adapter.web.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.cookie")
data class CookieProperties(val secure: Boolean, val domain: String, val sameSite: String)
