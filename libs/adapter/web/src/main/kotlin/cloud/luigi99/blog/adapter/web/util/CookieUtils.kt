package cloud.luigi99.blog.adapter.web.util

import cloud.luigi99.blog.adapter.web.config.CookieProperties
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class CookieUtils(private val cookieProperties: CookieProperties) {
    fun createCookie(
        name: String,
        value: String,
        maxAge: Long,
        domain: String = cookieProperties.domain,
        secure: Boolean = cookieProperties.secure,
        sameSite: String = cookieProperties.sameSite,
    ): ResponseCookie =
        ResponseCookie
            .from(name, value)
            .path("/")
            .httpOnly(true)
            .secure(secure)
            .sameSite(sameSite)
            .domain(domain)
            .maxAge(maxAge)
            .build()

    fun deleteCookie(
        name: String,
        domain: String = cookieProperties.domain,
        secure: Boolean = cookieProperties.secure,
        sameSite: String = cookieProperties.sameSite,
    ): ResponseCookie =
        ResponseCookie
            .from(name, "")
            .path("/")
            .httpOnly(true)
            .secure(secure)
            .sameSite(sameSite)
            .domain(domain)
            .maxAge(0)
            .build()

    fun getCookie(request: HttpServletRequest, name: String): String? =
        request.cookies
            ?.find { it.name == name }
            ?.value
}
