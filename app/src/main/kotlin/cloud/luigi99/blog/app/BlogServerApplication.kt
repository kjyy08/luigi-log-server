package cloud.luigi99.blog.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "cloud.luigi99.blog.app",
        "cloud.luigi99.blog.adapter.persistence.jpa",
        "cloud.luigi99.blog.adapter.persistence.redis",
        "cloud.luigi99.blog.adapter.message.spring",
        "cloud.luigi99.blog.member.application",
        "cloud.luigi99.blog.member.adapter.in.web",
        "cloud.luigi99.blog.member.adapter.out.persistence.jpa",
        "cloud.luigi99.blog.member.adapter.out.persistence.redis",
        "cloud.luigi99.blog.member.adapter.out.auth.jwt",
        "cloud.luigi99.blog.content",
        "cloud.luigi99.blog.media",
        "cloud.luigi99.blog.common",
    ],
)
class BlogServerApplication

fun main(args: Array<String>) {
    runApplication<BlogServerApplication>(*args)
}
