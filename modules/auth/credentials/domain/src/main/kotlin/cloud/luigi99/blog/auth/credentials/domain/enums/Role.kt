package cloud.luigi99.blog.auth.credentials.domain.enums

enum class Role {
    USER,
    ADMIN,
    ;

    val authority: String
        get() = "ROLE_$name"

    companion object {
        fun getDefault(): Role = USER
    }
}
