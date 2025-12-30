# Adapter/Out/Persistence Patterns

## JPA Entity

```kotlin
@Entity
@Table(name = "member")
@DynamicUpdate
class MemberJpaEntity private constructor(
    @Id
    @Column(name = "id")
    val id: UUID,

    @Column(name = "email", nullable = false, unique = true)
    val email: String,

    @Column(name = "username", nullable = false, length = 100)
    val username: String,

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    var profile: ProfileJpaEntity? = null,

) : JpaAggregateRoot<MemberId>() {

    override val entityId: MemberId
        get() = MemberId(id)

    companion object {
        fun from(entityId: UUID, email: String, username: String): MemberJpaEntity =
            MemberJpaEntity(id = entityId, email = email, username = username)
    }
}
```

**Key patterns:**
- Extend `JpaAggregateRoot<ID>` (provides `createdAt`, `updatedAt`)
- Use `JpaDomainEntity<ID>` for non-root entities
- `@DynamicUpdate` - only update changed columns
- Private constructor + factory `from()`
- Primitive types (String, UUID) - ValueObjects unwrapped
- Override `entityId` to return ValueObject

## JPA Entity with JSON Column

```kotlin
@Entity
@Table(name = "profile")
@DynamicUpdate
class ProfileJpaEntity private constructor(
    @Id
    @Column(name = "id")
    val id: UUID,

    @Column(name = "nickname", nullable = false, length = 100)
    val nickname: String,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tech_stack", columnDefinition = "JSON")
    val techStack: List<String> = emptyList(),

) : JpaDomainEntity<ProfileId>() {

    override val entityId: ProfileId
        get() = ProfileId(id)

    companion object {
        fun from(entityId: UUID, nickname: String, techStack: List<String>): ProfileJpaEntity =
            ProfileJpaEntity(id = entityId, nickname = nickname, techStack = techStack)
    }
}
```

**For PostgreSQL JSON:**
- `@JdbcTypeCode(SqlTypes.JSON)`
- `columnDefinition = "JSON"`

## Spring Data JPA Repository

```kotlin
@Repository
interface MemberJpaRepository : JpaRepository<MemberJpaEntity, UUID> {

    @Query("SELECT m FROM MemberJpaEntity m WHERE m.email = :email")
    fun findByEmailValue(@Param("email") email: String): MemberJpaEntity?

    @Query("SELECT COUNT(m) > 0 FROM MemberJpaEntity m WHERE m.email = :email")
    fun existsByEmailValue(@Param("email") email: String): Boolean
}
```

**Pattern:**
- Extend `JpaRepository<Entity, UUID>`
- Use `@Query` for custom JPQL
- Method names: `findByXxxValue()` (unwrapped ValueObject)

## Mapper

```kotlin
object MemberMapper {

    fun toDomain(entity: MemberJpaEntity): Member =
        Member.from(
            entityId = MemberId(entity.id),
            email = Email(entity.email),
            username = Username(entity.username),
            profile = entity.profile?.let { ProfileMapper.toDomain(it) },
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun toEntity(member: Member): MemberJpaEntity {
        val memberJpaEntity = MemberJpaEntity
            .from(
                entityId = member.entityId.value,
                email = member.email.value,
                username = member.username.value,
            ).apply {
                createdAt = member.createdAt
                updatedAt = member.updatedAt
            }

        memberJpaEntity.profile = member.profile?.let { ProfileMapper.toEntity(it) }

        return memberJpaEntity
    }
}
```

**Key patterns:**
- `object` singleton (stateless)
- `toDomain()`: JPA → Domain (wrap in ValueObjects)
- `toEntity()`: Domain → JPA (unwrap ValueObjects)
- `?.let { }` for nullable conversions
- Explicitly set `createdAt`, `updatedAt`

## Repository Adapter

```kotlin
/**
 * 회원 저장소 어댑터 구현체
 *
 * JPA 리포지토리를 사용하여 회원 데이터를 영속화하고, 도메인 이벤트를 발행합니다.
 */
@Repository
class MemberRepositoryAdapter(
    private val jpaRepository: MemberJpaRepository,
    private val eventContextManager: EventContextManager,
    private val domainEventPublisher: DomainEventPublisher,
) : MemberRepository {

    /**
     * 회원 엔티티를 저장합니다.
     * 저장 후 누적된 도메인 이벤트를 발행합니다.
     */
    override fun save(entity: Member): Member {
        log.debug { "Saving member: ${entity.entityId}" }

        // 1. Domain → JPA
        val memberJpaEntity = MemberMapper.toEntity(entity)

        // 2. Save to DB
        val saved = jpaRepository.save(memberJpaEntity)

        // 3. Publish domain events
        val events = eventContextManager.getDomainEventsAndClear()
        events.forEach { domainEventPublisher.publish(it) }

        log.debug { "Successfully saved member: ${saved.entityId}" }

        // 4. JPA → Domain
        return MemberMapper.toDomain(saved)
    }

    /**
     * ID로 회원을 조회합니다.
     */
    override fun findById(id: MemberId): Member? {
        log.debug { "Finding member by ID: $id" }
        return jpaRepository.findById(id.value)
            .map { MemberMapper.toDomain(it) }
            .orElse(null)
    }

    /**
     * 이메일로 회원을 조회합니다.
     */
    override fun findByEmail(email: Email): Member? {
        log.debug { "Finding member by email: $email" }
        return jpaRepository.findByEmailValue(email.value)?.let { MemberMapper.toDomain(it) }
    }

    override fun existsByEmail(email: Email): Boolean =
        jpaRepository.existsByEmailValue(email.value)

    override fun deleteById(id: MemberId) {
        log.debug { "Deleting member by ID: $id" }
        jpaRepository.deleteById(id.value)
    }
}
```

**Key patterns:**
- Implement Repository Port interface
- Inject `JpaRepository`, `EventContextManager`, `DomainEventPublisher`
- `save()` publishes domain events:
  ```kotlin
  val events = eventContextManager.getDomainEventsAndClear()
  events.forEach { domainEventPublisher.publish(it) }
  ```
- Use Mapper for conversions
- Unwrap ValueObjects for JPA calls (`id.value`, `email.value`)
- **Korean KDoc for every method**
