package cloud.luigi99.blog.common.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    private val DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    fun now(): LocalDateTime = LocalDateTime.now()
    
    fun format(dateTime: LocalDateTime, formatter: DateTimeFormatter = DEFAULT_FORMATTER): String {
        return dateTime.format(formatter)
    }
    
    fun parse(dateTimeString: String, formatter: DateTimeFormatter = DEFAULT_FORMATTER): LocalDateTime {
        return LocalDateTime.parse(dateTimeString, formatter)
    }
}