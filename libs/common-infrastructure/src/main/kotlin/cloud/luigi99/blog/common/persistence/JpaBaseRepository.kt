package cloud.luigi99.blog.common.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.util.*

@NoRepositoryBean
interface JpaBaseRepository<T, ID : Any> : JpaRepository<T, ID> {
    fun findByIdOrThrow(id: ID): T {
        return findById(id).orElseThrow {
            throw IllegalArgumentException("Entity with id $id not found")
        }
    }
}