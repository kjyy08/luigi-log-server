package cloud.luigi99.blog.common.util

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.ConcurrentHashMap

/**
 * 블로그 플랫폼을 위한 날짜/시간 유틸리티
 * 시간대 처리, 날짜 연산, 다양한 포맷 지원, 성능 최적화가 적용된 유틸리티입니다.
 */
object DateUtils {

    // 캐시된 포맷터들 - 스레드 안전성과 성능을 위해
    private val formatters = ConcurrentHashMap<String, DateTimeFormatter>()

    // 미리 정의된 포맷터들
    val DEFAULT_FORMATTER: DateTimeFormatter = getOrCreateFormatter("yyyy-MM-dd HH:mm:ss")
    val ISO_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val RFC_FORMATTER: DateTimeFormatter = DateTimeFormatter.RFC_1123_DATE_TIME
    val KOREAN_DATE_FORMATTER: DateTimeFormatter = getOrCreateFormatter("yyyy년 MM월 dd일")
    val KOREAN_DATETIME_FORMATTER: DateTimeFormatter = getOrCreateFormatter("yyyy년 MM월 dd일 HH시 mm분")
    val BLOG_POST_FORMATTER: DateTimeFormatter = getOrCreateFormatter("MMM dd, yyyy")
    val FILE_NAME_FORMATTER: DateTimeFormatter = getOrCreateFormatter("yyyyMMdd_HHmmss")

    // 기본 시간대 - 한국 시간
    private val DEFAULT_ZONE_ID = ZoneId.of("Asia/Seoul")

    /**
     * 현재 시각 관련 함수들
     */
    fun now(): LocalDateTime = LocalDateTime.now()
    fun nowZoned(zoneId: ZoneId = DEFAULT_ZONE_ID): ZonedDateTime = ZonedDateTime.now(zoneId)
    fun nowKorea(): ZonedDateTime = ZonedDateTime.now(DEFAULT_ZONE_ID)
    fun nowUtc(): ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)
    fun nowInstant(): Instant = Instant.now()
    fun todayKorea(): LocalDate = LocalDate.now(DEFAULT_ZONE_ID)

    /**
     * 포맷팅 관련 함수들
     */
    fun format(dateTime: LocalDateTime, formatter: DateTimeFormatter = DEFAULT_FORMATTER): String {
        return dateTime.format(formatter)
    }

    fun format(dateTime: LocalDateTime, pattern: String): String {
        return dateTime.format(getOrCreateFormatter(pattern))
    }

    fun format(zonedDateTime: ZonedDateTime, formatter: DateTimeFormatter = DEFAULT_FORMATTER): String {
        return zonedDateTime.format(formatter)
    }

    fun formatForBlog(dateTime: LocalDateTime): String {
        return dateTime.format(BLOG_POST_FORMATTER)
    }

    fun formatKorean(dateTime: LocalDateTime): String {
        return dateTime.format(KOREAN_DATETIME_FORMATTER)
    }

    fun formatForFileName(dateTime: LocalDateTime = now()): String {
        return dateTime.format(FILE_NAME_FORMATTER)
    }

    /**
     * 파싱 관련 함수들
     */
    fun parse(dateTimeString: String, formatter: DateTimeFormatter = DEFAULT_FORMATTER): LocalDateTime {
        return LocalDateTime.parse(dateTimeString, formatter)
    }

    fun parse(dateTimeString: String, pattern: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeString, getOrCreateFormatter(pattern))
    }

    fun parseZoned(dateTimeString: String, formatter: DateTimeFormatter = RFC_FORMATTER): ZonedDateTime {
        return ZonedDateTime.parse(dateTimeString, formatter)
    }

    fun tryParse(dateTimeString: String, vararg patterns: String): LocalDateTime? {
        for (pattern in patterns) {
            try {
                return parse(dateTimeString, pattern)
            } catch (e: Exception) {
                continue
            }
        }
        return null
    }

    /**
     * 날짜 연산 관련 함수들
     */
    fun addDays(dateTime: LocalDateTime, days: Long): LocalDateTime = dateTime.plusDays(days)
    fun addHours(dateTime: LocalDateTime, hours: Long): LocalDateTime = dateTime.plusHours(hours)
    fun addMinutes(dateTime: LocalDateTime, minutes: Long): LocalDateTime = dateTime.plusMinutes(minutes)
    fun addWeeks(dateTime: LocalDateTime, weeks: Long): LocalDateTime = dateTime.plusWeeks(weeks)
    fun addMonths(dateTime: LocalDateTime, months: Long): LocalDateTime = dateTime.plusMonths(months)

    fun subtractDays(dateTime: LocalDateTime, days: Long): LocalDateTime = dateTime.minusDays(days)
    fun subtractHours(dateTime: LocalDateTime, hours: Long): LocalDateTime = dateTime.minusHours(hours)
    fun subtractMinutes(dateTime: LocalDateTime, minutes: Long): LocalDateTime = dateTime.minusMinutes(minutes)

    /**
     * 시간대 변환
     */
    fun toZonedDateTime(localDateTime: LocalDateTime, zoneId: ZoneId = DEFAULT_ZONE_ID): ZonedDateTime {
        return localDateTime.atZone(zoneId)
    }

    fun toKoreaTime(utcDateTime: ZonedDateTime): ZonedDateTime {
        return utcDateTime.withZoneSameInstant(DEFAULT_ZONE_ID)
    }

    fun toUtc(koreaDateTime: ZonedDateTime): ZonedDateTime {
        return koreaDateTime.withZoneSameInstant(ZoneOffset.UTC)
    }

    /**
     * 날짜 비교 및 검증
     */
    fun isAfter(dateTime1: LocalDateTime, dateTime2: LocalDateTime): Boolean = dateTime1.isAfter(dateTime2)
    fun isBefore(dateTime1: LocalDateTime, dateTime2: LocalDateTime): Boolean = dateTime1.isBefore(dateTime2)
    fun isEqual(dateTime1: LocalDateTime, dateTime2: LocalDateTime): Boolean = dateTime1.isEqual(dateTime2)

    fun isSameDay(dateTime1: LocalDateTime, dateTime2: LocalDateTime): Boolean {
        return dateTime1.toLocalDate() == dateTime2.toLocalDate()
    }

    fun isToday(dateTime: LocalDateTime, zoneId: ZoneId = DEFAULT_ZONE_ID): Boolean {
        return dateTime.toLocalDate() == LocalDate.now(zoneId)
    }

    fun isYesterday(dateTime: LocalDateTime, zoneId: ZoneId = DEFAULT_ZONE_ID): Boolean {
        return dateTime.toLocalDate() == LocalDate.now(zoneId).minusDays(1)
    }

    fun isThisWeek(dateTime: LocalDateTime, zoneId: ZoneId = DEFAULT_ZONE_ID): Boolean {
        val now = LocalDate.now(zoneId)
        val startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        val targetDate = dateTime.toLocalDate()

        return !targetDate.isBefore(startOfWeek) && !targetDate.isAfter(endOfWeek)
    }

    fun isThisMonth(dateTime: LocalDateTime, zoneId: ZoneId = DEFAULT_ZONE_ID): Boolean {
        val now = LocalDate.now(zoneId)
        val targetDate = dateTime.toLocalDate()
        return now.year == targetDate.year && now.month == targetDate.month
    }

    fun isValidDate(year: Int, month: Int, day: Int): Boolean {
        return try {
            LocalDate.of(year, month, day)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 기간 계산
     */
    fun daysBetween(start: LocalDateTime, end: LocalDateTime): Long {
        return ChronoUnit.DAYS.between(start, end)
    }

    fun hoursBetween(start: LocalDateTime, end: LocalDateTime): Long {
        return ChronoUnit.HOURS.between(start, end)
    }

    fun minutesBetween(start: LocalDateTime, end: LocalDateTime): Long {
        return ChronoUnit.MINUTES.between(start, end)
    }

    fun secondsBetween(start: LocalDateTime, end: LocalDateTime): Long {
        return ChronoUnit.SECONDS.between(start, end)
    }

    /**
     * 블로그 특화 함수들
     */
    fun getRelativeTimeString(dateTime: LocalDateTime, baseTime: LocalDateTime = now()): String {
        val minutes = minutesBetween(dateTime, baseTime)

        return when {
            minutes < 1 -> "방금 전"
            minutes < 60 -> "${minutes}분 전"
            minutes < 1440 -> "${minutes / 60}시간 전"
            minutes < 10080 -> "${minutes / 1440}일 전"
            minutes < 43200 -> "${minutes / 10080}주 전"
            else -> formatKorean(dateTime)
        }
    }

    fun getReadingTimeCategory(publishedAt: LocalDateTime): String {
        val now = now()
        val days = daysBetween(publishedAt, now)

        return when {
            days <= 1 -> "최신"
            days <= 7 -> "이번 주"
            days <= 30 -> "이번 달"
            days <= 365 -> "올해"
            else -> "지난해"
        }
    }

    /**
     * 주기적 작업을 위한 유틸리티
     */
    fun isScheduledTime(cronExpression: String, checkTime: LocalDateTime = now()): Boolean {
        // 간단한 cron 표현식 검증 (시 분 형태만)
        // 예: "0 9" = 매일 오전 9시
        val parts = cronExpression.split(" ")
        if (parts.size != 2) return false

        val targetMinute = parts[0].toIntOrNull() ?: return false
        val targetHour = parts[1].toIntOrNull() ?: return false

        return checkTime.minute == targetMinute && checkTime.hour == targetHour
    }

    fun getStartOfDay(dateTime: LocalDateTime): LocalDateTime {
        return dateTime.truncatedTo(ChronoUnit.DAYS)
    }

    fun getEndOfDay(dateTime: LocalDateTime): LocalDateTime {
        return dateTime.truncatedTo(ChronoUnit.DAYS).plusDays(1).minusNanos(1)
    }

    fun getStartOfWeek(dateTime: LocalDateTime): LocalDateTime {
        return dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .truncatedTo(ChronoUnit.DAYS)
    }

    fun getStartOfMonth(dateTime: LocalDateTime): LocalDateTime {
        return dateTime.with(TemporalAdjusters.firstDayOfMonth())
            .truncatedTo(ChronoUnit.DAYS)
    }

    /**
     * 내부 헬퍼 함수들
     */
    private fun getOrCreateFormatter(pattern: String): DateTimeFormatter {
        return formatters.computeIfAbsent(pattern) { DateTimeFormatter.ofPattern(it) }
    }

    /**
     * Extension functions
     */
    fun LocalDateTime.toKoreanString(): String = this.format(KOREAN_DATETIME_FORMATTER)
    fun LocalDateTime.toBlogString(): String = this.format(BLOG_POST_FORMATTER)
    fun LocalDateTime.toRelativeString(baseTime: LocalDateTime = now()): String = getRelativeTimeString(this, baseTime)
    fun LocalDateTime.isToday(): Boolean = isToday(this)
    fun LocalDateTime.isYesterday(): Boolean = isYesterday(this)
}