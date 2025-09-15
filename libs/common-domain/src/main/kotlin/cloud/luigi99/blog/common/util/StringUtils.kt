package cloud.luigi99.blog.common.util

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.text.Normalizer
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.ceil
import kotlin.text.Regex

/**
 * 블로그 플랫폼을 위한 문자열 유틸리티
 * 한글 처리, 보안, 텍스트 분석, SEO 최적화, 마크다운 처리가 적용된 유틸리티입니다.
 */
object StringUtils {

    // 캐시된 정규식들 - 성능 최적화
    private val regexCache = ConcurrentHashMap<String, Regex>()

    // 미리 정의된 정규식들
    private val EMAIL_REGEX = getOrCreateRegex("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")
    private val URL_REGEX = getOrCreateRegex("https?://[\\w\\-._~:/?#\\[\\]@!\\$&'()*+,;=%]+")
    private val KOREAN_REGEX = getOrCreateRegex("[가-힣]")
    private val HTML_TAG_REGEX = getOrCreateRegex("<[^>]*>")
    private val MARKDOWN_LINK_REGEX = getOrCreateRegex("\\[([^\\]]+)\\]\\([^\\)]+\\)")
    private val WHITESPACE_REGEX = getOrCreateRegex("\\s+")
    private val SLUG_CLEAN_REGEX = getOrCreateRegex("[^a-z0-9가-힣]+")
    private val MULTIPLE_DASH_REGEX = getOrCreateRegex("-+")

    // 한글 배열상수
    private const val HANGUL_START = 0xAC00
    private const val HANGUL_END = 0xD7A3
    private const val HANGUL_BASE = 0xAC00

    // 한글 초성, 중성, 종성 배열
    private val CHOSUNG = charArrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
        'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    private val JUNGSUNG = charArrayOf(
        'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
        'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
    )

    private val JONGSUNG = charArrayOf(
        ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ',
        'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
        'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    /**
     * 기본 문자열 검증
     */
    fun isBlank(str: String?): Boolean = str.isNullOrBlank()
    fun isNotBlank(str: String?): Boolean = !isBlank(str)
    fun isEmpty(str: String?): Boolean = str.isNullOrEmpty()
    fun isNotEmpty(str: String?): Boolean = !isEmpty(str)

    fun hasText(str: String?): Boolean {
        return str != null && str.isNotBlank() && str.trim().isNotEmpty()
    }

    fun defaultIfBlank(str: String?, defaultValue: String): String {
        return if (isBlank(str)) defaultValue else str!!
    }

    fun defaultIfEmpty(str: String?, defaultValue: String): String {
        return if (isEmpty(str)) defaultValue else str!!
    }

    /**
     * 문자열 조작
     */
    fun truncate(str: String, maxLength: Int, suffix: String = "..."): String {
        return if (str.length <= maxLength) {
            str
        } else {
            str.substring(0, maxLength - suffix.length) + suffix
        }
    }

    fun truncateWords(str: String, maxWords: Int, suffix: String = "..."): String {
        val words = str.split(WHITESPACE_REGEX)
        return if (words.size <= maxWords) {
            str
        } else {
            words.take(maxWords).joinToString(" ") + suffix
        }
    }

    fun capitalize(str: String): String {
        return str.lowercase().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }

    fun camelCase(str: String): String {
        return str.split("[\\s_-]+".toRegex())
            .mapIndexed { index, word ->
                if (index == 0) word.lowercase()
                else word.lowercase().replaceFirstChar { it.uppercase() }
            }
            .joinToString("")
    }

    fun kebabCase(str: String): String {
        return str.replace("([a-z])([A-Z])".toRegex(), "$1-$2")
            .lowercase()
            .replace("[\\s_]+".toRegex(), "-")
    }

    fun snakeCase(str: String): String {
        return str.replace("([a-z])([A-Z])".toRegex(), "$1_$2")
            .lowercase()
            .replace("[\\s-]+".toRegex(), "_")
    }

    /**
     * 한글 처리 기능
     */
    fun isKorean(char: Char): Boolean {
        return char.code in HANGUL_START..HANGUL_END
    }

    fun hasKorean(str: String): Boolean {
        return KOREAN_REGEX.containsMatchIn(str)
    }

    fun extractChosung(str: String): String {
        return str.map { char ->
            if (isKorean(char)) {
                val code = char.code - HANGUL_BASE
                val chosungIndex = code / (21 * 28)
                CHOSUNG[chosungIndex]
            } else {
                char
            }
        }.joinToString("")
    }

    fun decomposeHangul(char: Char): Triple<Char, Char, Char> {
        if (!isKorean(char)) {
            return Triple(char, ' ', ' ')
        }

        val code = char.code - HANGUL_BASE
        val chosungIndex = code / (21 * 28)
        val jungsungIndex = (code % (21 * 28)) / 28
        val jongsungIndex = code % 28

        return Triple(
            CHOSUNG[chosungIndex],
            JUNGSUNG[jungsungIndex],
            JONGSUNG[jongsungIndex]
        )
    }

    fun normalizeKorean(str: String): String {
        return Normalizer.normalize(str, Normalizer.Form.NFC)
    }

    /**
     * SEO 및 블로그 특화 기능
     */
    fun slugify(str: String, maxLength: Int = 100): String {
        return str.lowercase()
            .replace(SLUG_CLEAN_REGEX, "-")
            .replace(MULTIPLE_DASH_REGEX, "-")
            .trim('-')
            .let { if (it.length > maxLength) it.substring(0, maxLength).trim('-') else it }
    }

    fun createSeoFriendlySlug(title: String, includeDate: Boolean = true): String {
        val baseSlug = slugify(title, 80)
        return if (includeDate) {
            "${DateUtils.format(DateUtils.now(), "yyyy-MM-dd")}-$baseSlug"
        } else {
            baseSlug
        }
    }

    fun generateMetaDescription(content: String, maxLength: Int = 160): String {
        val cleanContent = removeHtmlTags(content)
            .replace("\\n+".toRegex(), " ")
            .replace(WHITESPACE_REGEX, " ")
            .trim()

        return truncate(cleanContent, maxLength)
    }

    /**
     * 보안 관련 기능
     */
    fun maskEmail(email: String): String {
        val parts = email.split("@")
        if (parts.size != 2) return email

        val username = parts[0]
        val domain = parts[1]
        val maskedUsername = if (username.length <= 3) {
            username[0] + "*".repeat(username.length - 1)
        } else {
            username.take(2) + "*".repeat(username.length - 3) + username.takeLast(1)
        }

        return "$maskedUsername@$domain"
    }

    fun maskPhoneNumber(phone: String): String {
        val cleanPhone = phone.replace("[^0-9]".toRegex(), "")
        return when (cleanPhone.length) {
            10 -> "${cleanPhone.substring(0, 3)}-***-${cleanPhone.substring(6)}"
            11 -> "${cleanPhone.substring(0, 3)}-****-${cleanPhone.substring(7)}"
            else -> phone
        }
    }

    fun escapeHtml(str: String): String {
        return str.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;")
    }

    fun escapeSql(str: String): String {
        return str.replace("'", "''")
            .replace("\\", "\\\\")
            .replace("\u0000", "\\0")
            .replace("\r", "\\r")
            .replace("\n", "\\n")
            .replace("\t", "\\t")
            .replace("\u001a", "\\Z")
    }

    fun generateSecureToken(length: Int = 32): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = java.security.SecureRandom()
        return (1..length)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
    }

    fun hashString(input: String, algorithm: String = "SHA-256"): String {
        val digest = MessageDigest.getInstance(algorithm)
        val hashBytes = digest.digest(input.toByteArray(StandardCharsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * 유효성 검증
     */
    fun isValidEmail(email: String): Boolean {
        return EMAIL_REGEX.matches(email)
    }

    fun isValidUrl(url: String): Boolean {
        return URL_REGEX.matches(url)
    }

    fun isValidSlug(slug: String): Boolean {
        return slug.matches("^[a-z0-9가-힣]+(?:-[a-z0-9가-힣]+)*$".toRegex())
    }

    fun extractUrls(text: String): List<String> {
        return URL_REGEX.findAll(text).map { it.value }.toList()
    }

    fun extractEmails(text: String): List<String> {
        return EMAIL_REGEX.findAll(text).map { it.value }.toList()
    }

    /**
     * 텍스트 분석
     */
    fun countWords(text: String): Int {
        if (isBlank(text)) return 0
        val cleanText = removeHtmlTags(text).trim()
        if (cleanText.isEmpty()) return 0

        // 한글과 영어 단어를 다르게 처리
        val koreanWords = cleanText.filter { isKorean(it) }.length
        val englishWords = cleanText.split(WHITESPACE_REGEX)
            .filter { it.matches("[a-zA-Z]+".toRegex()) }.size

        return koreanWords + englishWords
    }

    fun countCharacters(text: String, includeSpaces: Boolean = true): Int {
        val cleanText = removeHtmlTags(text)
        return if (includeSpaces) cleanText.length else cleanText.replace("\\s".toRegex(), "").length
    }

    fun estimateReadingTime(text: String, wordsPerMinute: Int = 200): Int {
        val wordCount = countWords(text)
        return ceil(wordCount.toDouble() / wordsPerMinute).toInt().coerceAtLeast(1)
    }

    fun analyzeSentiment(text: String): String {
        // 간단한 감정 분석 - 긍정/부정 단어 비율
        val positiveWords = listOf("좋", "훌륭", "만족", "추천", "대단", "성공", "good", "great", "excellent", "amazing")
        val negativeWords = listOf("나쁜", "실망", "문제", "어려운", "실패", "bad", "terrible", "awful", "disappointing")

        val lowerText = text.lowercase()
        val positiveCount = positiveWords.count { lowerText.contains(it) }
        val negativeCount = negativeWords.count { lowerText.contains(it) }

        return when {
            positiveCount > negativeCount -> "positive"
            negativeCount > positiveCount -> "negative"
            else -> "neutral"
        }
    }

    /**
     * 마크다운 처리
     */
    fun removeMarkdown(text: String): String {
        return text
            .replace("^#+\\s*".toRegex(RegexOption.MULTILINE), "") // 헤더
            .replace("\\*\\*([^*]+)\\*\\*".toRegex(), "$1") // bold
            .replace("\\*([^*]+)\\*".toRegex(), "$1") // italic
            .replace("`([^`]+)`".toRegex(), "$1") // inline code
            .replace(MARKDOWN_LINK_REGEX, "$1") // links
            .replace("^>\\s*".toRegex(RegexOption.MULTILINE), "") // blockquotes
            .replace("^[-*+]\\s*".toRegex(RegexOption.MULTILINE), "") // lists
            .replace("^\\d+\\.\\s*".toRegex(RegexOption.MULTILINE), "") // ordered lists
            .replace("```[\\s\\S]*?```".toRegex(), "") // code blocks
            .trim()
    }

    fun removeHtmlTags(text: String): String {
        return text.replace(HTML_TAG_REGEX, "")
            .replace("&nbsp;", " ")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#x27;", "'")
    }

    fun extractPlainText(markdownOrHtml: String): String {
        return removeHtmlTags(removeMarkdown(markdownOrHtml))
            .replace(WHITESPACE_REGEX, " ")
            .trim()
    }

    /**
     * 검색 및 하이라이트
     */
    fun highlightSearchTerms(text: String, searchTerms: List<String>, highlightClass: String = "highlight"): String {
        var result = text
        searchTerms.forEach { term ->
            val regex = "(?i)($term)".toRegex()
            result = result.replace(regex, "<span class=\"$highlightClass\">$1</span>")
        }
        return result
    }

    fun createSearchSnippet(content: String, searchTerm: String, snippetLength: Int = 200): String {
        val lowerContent = content.lowercase()
        val lowerTerm = searchTerm.lowercase()
        val index = lowerContent.indexOf(lowerTerm)

        if (index == -1) {
            return truncate(content, snippetLength)
        }

        val start = (index - snippetLength / 4).coerceAtLeast(0)
        val end = (index + searchTerm.length + snippetLength * 3 / 4).coerceAtMost(content.length)

        val snippet = content.substring(start, end)
        val prefix = if (start > 0) "..." else ""
        val suffix = if (end < content.length) "..." else ""

        return prefix + snippet + suffix
    }

    /**
     * URL 및 인코딩
     */
    fun urlEncode(str: String): String {
        return URLEncoder.encode(str, StandardCharsets.UTF_8.toString())
    }

    fun createShareUrl(baseUrl: String, title: String, summary: String = ""): String {
        val encodedTitle = urlEncode(title)
        val encodedSummary = if (summary.isNotBlank()) urlEncode(summary) else ""

        return if (encodedSummary.isNotBlank()) {
            "$baseUrl?title=$encodedTitle&summary=$encodedSummary"
        } else {
            "$baseUrl?title=$encodedTitle"
        }
    }

    /**
     * 내부 헬퍼 함수들
     */
    private fun getOrCreateRegex(pattern: String): Regex {
        return regexCache.computeIfAbsent(pattern) { Regex(it) }
    }

    /**
     * Extension functions
     */
    fun String.toSlug(): String = slugify(this)
    fun String.toKebabCase(): String = kebabCase(this)
    fun String.toCamelCase(): String = camelCase(this)
    fun String.toSnakeCase(): String = snakeCase(this)
    fun String.containsKorean(): Boolean = hasKorean(this)
    fun String.getChosung(): String = extractChosung(this)
    fun String.stripMarkdown(): String = removeMarkdown(this)
    fun String.stripHtml(): String = removeHtmlTags(this)
    fun String.toPlainText(): String = extractPlainText(this)
    fun String.wordCount(): Int = countWords(this)
    fun String.readingTime(): Int = estimateReadingTime(this)
    fun String.isEmail(): Boolean = isValidEmail(this)
    fun String.isUrl(): Boolean = isValidUrl(this)
    fun String.maskedEmail(): String = maskEmail(this)
    fun String.highlightTerms(terms: List<String>): String = highlightSearchTerms(this, terms)
}