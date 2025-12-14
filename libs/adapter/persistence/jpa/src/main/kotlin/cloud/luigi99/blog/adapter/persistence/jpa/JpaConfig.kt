package cloud.luigi99.blog.adapter.persistence.jpa

import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(
    basePackages = [
        "cloud.luigi99.blog",
    ],
)
@EntityScan(
    basePackages = [
        "cloud.luigi99.blog",
    ],
)
class JpaConfig
