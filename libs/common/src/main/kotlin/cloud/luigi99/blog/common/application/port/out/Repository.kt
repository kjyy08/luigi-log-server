package cloud.luigi99.blog.common.application.port.out

import cloud.luigi99.blog.common.domain.AggregateRoot
import cloud.luigi99.blog.common.domain.ValueObject

interface Repository<T : AggregateRoot<ID>, ID : ValueObject> {
    fun findById(id: ID): T?

    fun deleteById(id: ID)

    fun save(entity: T): T
}
