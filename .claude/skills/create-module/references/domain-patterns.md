# Domain Patterns

## Aggregate Root

**Example: Member.kt**

```kotlin
/**
 * 회원 도메인 엔티티
 *
 * 회원의 핵심 정보와 행위를 정의합니다.
 */
class Member private constructor(
    override val entityId: MemberId,
    val email: Email,
    val username: Username,
) : AggregateRoot<MemberId>() {

    companion object {
        /**
         * 신규 회원을 등록합니다.
         */
        fun register(email: Email, username: Username): Member {
            val member = Member(
                entityId = MemberId.generate(),
                email = email,
                username = username,
            )
            member.registerEvent(MemberRegisteredEvent(member.entityId, member.email))
            return member
        }

        /**
         * 영속성 계층에서 데이터를 로드하여 도메인 엔티티를 재구성합니다.
         */
        fun from(
            entityId: MemberId,
            email: Email,
            username: Username,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Member {
            val member = Member(entityId, email, username)
            member.createdAt = createdAt
            member.updatedAt = updatedAt
            return member
        }
    }

    /**
     * 사용자 이름을 변경합니다.
     */
    fun updateUsername(newUsername: Username): Member {
        val updated = Member(entityId, email, newUsername)
        updated.createdAt = createdAt
        updated.updatedAt = updatedAt
        updated.registerEvent(MemberUsernameUpdatedEvent(entityId, newUsername.value))
        return updated
    }
}
```

**Key patterns:**
- Private constructor + factory methods (`register()`, `from()`)
- `AggregateRoot<ID>` - provides `registerEvent()`
- Immutability - return new instance on state change
- **Korean KDoc on every class and method**

## Value Object

### ID Value Object

```kotlin
@JvmInline
value class MemberId(val value: UUID) : ValueObject, Serializable {
    companion object {
        fun generate(): MemberId = MemberId(UUID.randomUUID())
        fun from(value: String): MemberId = MemberId(UUID.fromString(value))
    }
}
```

### Validated Value Object

```kotlin
@JvmInline
value class Email(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Email cannot be blank" }
        require(value.matches(EMAIL_REGEX)) { "Invalid email format: $value" }
    }

    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    }
}
```

### Length-Constrained Value Object

```kotlin
@JvmInline
value class Username(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Username cannot be blank" }
        require(value.length in 2..100) {
            "Username must be between 2 and 100 characters (actual: ${value.length})"
        }
    }
}
```

### Collection Value Object

```kotlin
data class TechStack(val values: List<String>) : ValueObject {
    init {
        require(values.all { it.isNotBlank() }) { "Tech stack items cannot be blank" }
        require(values.size <= 50) { "Tech stack cannot exceed 50 items" }
    }

    fun isEmpty(): Boolean = values.isEmpty()
}
```

**Key patterns:**
- `@JvmInline value class` for single-field VOs (performance)
- `data class` for multi-field VOs
- `init` block validates invariants
- ID VOs: `generate()` + `from(String)`

## Domain Entity

```kotlin
class Profile private constructor(
    override val entityId: ProfileId,
    val nickname: Nickname,
    val bio: Bio?,
) : DomainEntity<ProfileId>() {

    companion object {
        fun create(nickname: Nickname, bio: Bio? = null): Profile =
            Profile(ProfileId.generate(), nickname, bio)

        fun from(
            entityId: ProfileId,
            nickname: Nickname,
            bio: Bio?,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Profile {
            val profile = Profile(entityId, nickname, bio)
            profile.createdAt = createdAt
            profile.updatedAt = updatedAt
            return profile
        }
    }

    fun update(nickname: Nickname = this.nickname, bio: Bio? = this.bio): Profile {
        val updated = Profile(entityId, nickname, bio)
        updated.createdAt = createdAt
        updated.updatedAt = updatedAt
        return updated
    }
}
```

**Difference from Aggregate Root:**
- Inherits `DomainEntity<ID>` (not `AggregateRoot`)
- No event registration (parent Aggregate handles events)
- Owned by Aggregate Root (e.g., Profile owned by Member)

## Domain Event

```kotlin
data class MemberRegisteredEvent(val memberId: MemberId, val email: Email) : DomainEvent

data class MemberUsernameUpdatedEvent(val memberId: MemberId, val username: String) : DomainEvent
```

**Trigger from Aggregate:**
```kotlin
member.registerEvent(MemberRegisteredEvent(member.entityId, member.email))
```

## Domain Exception

```kotlin
class MemberNotFoundException(message: String = "Member not found") :
    BusinessException(ErrorCode.MEMBER_NOT_FOUND, message)
```

**Pattern:**
- Extend `BusinessException`
- Provide default message
- Use `ErrorCode` enum
