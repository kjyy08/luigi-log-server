# Application Patterns

## UseCase Interface (Command)

```kotlin
interface RegisterMemberUseCase {
    fun execute(command: Command): Response

    data class Command(val email: String, val username: String)

    data class Response(val memberId: String, val email: String, val username: String)
}
```

**Pattern:**
- Nested `Command` and `Response` data classes
- String-based data (DTO-like)
- Single method: `execute()`

## UseCase Interface (Query)

```kotlin
interface GetCurrentMemberUseCase {
    fun execute(query: Query): Response

    data class Query(val memberId: String)

    data class Response(val memberId: String, val email: String, val username: String)
}
```

## Facade Interface

```kotlin
interface MemberCommandFacade {
    fun updateProfile(): UpdateMemberProfileUseCase
    fun registerMember(): RegisterMemberUseCase
    fun deleteMember(): DeleteMemberUseCase
}

interface MemberQueryFacade {
    fun getCurrentMember(): GetCurrentMemberUseCase
    fun getMemberProfile(): GetMemberProfileUseCase
}
```

**Purpose:** Group related UseCases, reduce Controller dependencies

## Service Implementation

```kotlin
/**
 * 회원 등록 유스케이스 구현체
 *
 * OAuth2 인증을 통한 신규 회원 등록을 처리합니다.
 */
@Service
class RegisterMemberService(private val memberRepository: MemberRepository) : RegisterMemberUseCase {

    @Transactional
    override fun execute(command: RegisterMemberUseCase.Command): RegisterMemberUseCase.Response {
        log.info { "Registering new member with email: ${command.email}" }

        // 1. Create Value Objects (validation happens here)
        val email = Email(command.email)
        val username = Username(command.username)

        // 2. Use Domain model
        val newMember = Member.register(email, username)

        // 3. Save
        val savedMember = memberRepository.save(newMember)

        log.info { "Successfully registered member: ${savedMember.entityId}" }

        // 4. Return Response
        return RegisterMemberUseCase.Response(
            memberId = savedMember.entityId.value.toString(),
            email = savedMember.email.value,
            username = savedMember.username.value,
        )
    }
}
```

**Key patterns:**
- `@Service` + `@Transactional`
- String → ValueObject conversion (validation)
- Domain model orchestration
- Repository interaction
- ValueObject → String for Response
- **Korean KDoc for every service**

## Facade Implementation

```kotlin
@Service
class MemberCommandService(
    private val updateMemberProfileUseCase: UpdateMemberProfileUseCase,
    private val registerMemberUseCase: RegisterMemberUseCase,
    private val deleteMemberUseCase: DeleteMemberUseCase,
) : MemberCommandFacade {

    override fun updateProfile(): UpdateMemberProfileUseCase = updateMemberProfileUseCase
    override fun registerMember(): RegisterMemberUseCase = registerMemberUseCase
    override fun deleteMember(): DeleteMemberUseCase = deleteMemberUseCase
}
```

## Repository Port

```kotlin
interface MemberRepository : Repository<Member, MemberId> {
    fun findByEmail(email: Email): Member?
    fun existsByEmail(email: Email): Boolean
}
```

**Pattern:**
- Extend `Repository<Entity, ID>` (provides `save()`, `findById()`, `deleteById()`)
- Use Domain types (Member, Email, MemberId)
- Add domain-specific queries
