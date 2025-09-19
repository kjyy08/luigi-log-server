package cloud.luigi99.blog.common.util

/**
 * 문자열 관련 유틸리티 함수들
 */
object StringUtils {

    /**
     * 문자열이 null이거나 공백인지 확인
     */
    fun isBlank(str: String?): Boolean {
        return str.isNullOrBlank()
    }

    /**
     * 문자열이 null이 아니고 공백이 아닌지 확인
     */
    fun isNotBlank(str: String?): Boolean {
        return !isBlank(str)
    }

    /**
     * 문자열을 카멜케이스로 변환
     */
    fun toCamelCase(str: String): String {
        if (isBlank(str)) return str

        return str.split("_", "-", " ")
            .joinToString("") { part ->
                if (part.isEmpty()) part
                else part.first().uppercaseChar() + part.drop(1).lowercase()
            }
            .replaceFirstChar { it.lowercase() }
    }

    /**
     * 문자열을 스네이크케이스로 변환
     */
    fun toSnakeCase(str: String): String {
        if (isBlank(str)) return str

        return str.replace(Regex("([a-z])([A-Z])"), "$1_$2")
            .lowercase()
    }
}