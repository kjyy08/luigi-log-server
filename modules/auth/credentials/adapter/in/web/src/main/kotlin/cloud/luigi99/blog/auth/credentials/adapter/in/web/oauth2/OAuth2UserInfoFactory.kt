package cloud.luigi99.blog.auth.credentials.adapter.`in`.web.oauth2

import cloud.luigi99.blog.auth.credentials.adapter.`in`.web.dto.GitHubOAuth2UserInfo
import org.springframework.security.oauth2.core.user.OAuth2User

object OAuth2UserInfoFactory {
    fun getOAuth2UserInfo(provider: String, oauth2User: OAuth2User): OAuth2UserInfo =
        when (provider.lowercase()) {
            "github" -> GitHubOAuth2UserInfo(oauth2User.attributes)
            else -> throw IllegalStateException("지원하지 않는 OAuth2 프로바이더: $provider")
        }
}
