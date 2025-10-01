package cloud.luigi99.blog.common.fixtures

import cloud.luigi99.blog.common.persistence.JpaBaseRepository
import java.util.*

/**
 * 테스트용 JPA Repository 인터페이스
 *
 * JpaBaseRepository 관련 테스트에서 사용되는 Repository 인터페이스입니다.
 */
interface TestJpaRepository : JpaBaseRepository<TestJpaEntity, TestEntityId, UUID>