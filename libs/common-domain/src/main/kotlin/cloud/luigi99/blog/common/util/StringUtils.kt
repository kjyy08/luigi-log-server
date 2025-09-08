package cloud.luigi99.blog.common.util

object StringUtils {
    fun isBlank(str: String?): Boolean = str.isNullOrBlank()
    
    fun isNotBlank(str: String?): Boolean = !isBlank(str)
    
    fun slugify(str: String): String {
        return str.lowercase()
            .replace(Regex("[^a-z0-9가-힣]"), "-")
            .replace(Regex("-+"), "-")
            .trim('-')
    }
    
    fun truncate(str: String, maxLength: Int, suffix: String = "..."): String {
        return if (str.length <= maxLength) {
            str
        } else {
            str.substring(0, maxLength - suffix.length) + suffix
        }
    }
}