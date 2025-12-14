package cloud.luigi99.blog.auth.token.adapter.out.persistence.redis.authtoken

import org.springframework.data.repository.CrudRepository

interface AuthTokenRedisRepository : CrudRepository<AuthTokenRedisEntity, String>
