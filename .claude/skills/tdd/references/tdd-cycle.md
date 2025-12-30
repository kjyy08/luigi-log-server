# TDD Cycle

## Red → Green → Refactor

### Red: Write Failing Test

1. **Write one test at a time** - smallest increment
2. **Use Korean Given-When-Then** - clear business intent
3. **Run test** - ensure it fails for the right reason
4. **Check failure message** - should be clear

**Example:**
```kotlin
Given("유효한 이메일과 사용자 이름이 주어졌을 때") {
    val email = Email("user@example.com")
    val username = Username("john_doe")

    When("회원을 등록하면") {
        val member = Member.register(email, username)

        Then("회원 ID가 생성된다") {
            member.entityId shouldNotBe null  // This will fail initially
        }
    }
}
```

### Green: Make It Pass

1. **Write minimal code** - simplest solution that makes test pass
2. **Don't worry about duplication yet** - refactor later
3. **Run test** - confirm green
4. **Run all tests** - ensure nothing broke

**Example:**
```kotlin
class Member private constructor(
    override val entityId: MemberId,
    val email: Email,
    val username: Username,
) : AggregateRoot<MemberId>() {

    companion object {
        fun register(email: Email, username: Username): Member {
            return Member(
                entityId = MemberId.generate(),  // Simplest implementation
                email = email,
                username = username,
            )
        }
    }
}
```

### Refactor: Improve Structure

1. **Only refactor when tests are green**
2. **Remove duplication**
3. **Improve names and structure**
4. **Run tests after each change**
5. **Stop when tests pass and code is clean**

**Example:**
```kotlin
// After tests pass, add domain event
companion object {
    fun register(email: Email, username: Username): Member {
        val member = Member(
            entityId = MemberId.generate(),
            email = email,
            username = username,
        )
        member.registerEvent(MemberRegisteredEvent(member.entityId, member.email))  // Refactor: add event
        return member
    }
}
```

## Checklist

### Red
- [ ] Test uses Korean Given-When-Then
- [ ] Test fails
- [ ] Failure message is clear
- [ ] Test is for smallest increment

### Green
- [ ] Minimal code added
- [ ] Single test passes
- [ ] All tests pass

### Refactor
- [ ] All tests still pass
- [ ] Duplication removed
- [ ] Names improved
- [ ] Structure clean
