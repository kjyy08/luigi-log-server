package cloud.luigi99.blog.auth.credentials.adapter.`in`.web.dto

import cloud.luigi99.blog.auth.credentials.adapter.`in`.web.oauth2.OAuth2UserInfo

data class GitHubOAuth2UserInfo(private val attributes: Map<String, Any>) : OAuth2UserInfo {
    override fun getEmail(): String =
        attributes["email"] as? String
            ?: throw IllegalStateException("GitHub 사용자 이메일을 찾을 수 없습니다")

    override fun getUsername(): String =
        (attributes["login"] as? String)
            ?: (attributes["name"] as? String)
            ?: "github_user"

    override fun getProviderId(): String =
        attributes["id"]?.toString()
            ?: throw IllegalStateException("GitHub provider ID를 찾을 수 없습니다")

    override fun getProfileImgUrl(): String =
        attributes["avatar_url"]?.toString()
            ?: throw IllegalStateException("GitHub 프로필 이미지 URL을 찾을 수 없습니다")
}
