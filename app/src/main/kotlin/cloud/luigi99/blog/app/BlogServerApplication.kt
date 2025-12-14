package cloud.luigi99.blog.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "cloud.luigi99.blog",
    ],
)
@ConfigurationPropertiesScan(
    basePackages = [
        "cloud.luigi99.blog",
    ],
)
class BlogServerApplication

fun main(args: Array<String>) {
    runApplication<BlogServerApplication>(*args)
}
