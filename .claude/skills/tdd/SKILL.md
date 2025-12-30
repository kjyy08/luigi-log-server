---
name: tdd
description: Guide Test-Driven Development following Kent Beck's Red-Green-Refactor cycle and Tidy First principles. Use when developing features with tests, following Kotest BehaviorSpec with Korean Given-When-Then patterns. Covers Domain (pure logic), Application (UseCase with MockK), and Adapter (Controller/Repository) layer testing.
---

# TDD Workflow

Follow Kent Beck's TDD cycle with Tidy First principles for blog-server project.

## Quick Start

**1. Red** → Write failing test
**2. Green** → Make it pass with minimal code
**3. Refactor** → Improve structure
**4. Repeat**

Always use **Kotest BehaviorSpec** with **Korean Given-When-Then**.

## Critical Pattern: BehaviorSpec

```kotlin
class MemberTest : BehaviorSpec({
    Given("유효한 이메일과 사용자 이름이 주어졌을 때") {
        When("회원을 등록하면") {
            Then("회원 ID가 생성된다") {
                member.entityId shouldNotBe null
            }
        }
    }
})
```

**Never use FunSpec or StringSpec** - only BehaviorSpec with Korean text.

## Workflow

### 1. Red: Write Failing Test

See [tdd-cycle.md](references/tdd-cycle.md) for details.

- Write smallest test in Korean Given-When-Then
- Run test: `./gradlew test --tests ClassName`
- Confirm it fails

### 2. Green: Make It Pass

- Write minimal code
- Run test: `./gradlew test --tests ClassName`
- Confirm it passes

### 3. Refactor: Improve

See [tidy-first-principles.md](references/tidy-first-principles.md) for Structural vs Behavioral changes.

- Remove duplication
- Improve names
- Run all tests: `./gradlew test`
- Confirm all pass

## Test Patterns by Layer

See [test-patterns.md](references/test-patterns.md) for complete code examples.

### Domain Layer

**Pure domain logic, no Spring:**

```kotlin
class EmailTest : BehaviorSpec({
    Given("빈 문자열이 주어졌을 때") {
        When("Email 객체를 생성하려고 하면") {
            val exception = shouldThrow<IllegalArgumentException> {
                Email("")
            }
            Then("예외가 발생한다") {
                exception.message shouldContain "Email cannot be blank"
            }
        }
    }
})
```

**Value Object Checklist:**
- ✅ Valid value
- ✅ Blank/empty validation
- ✅ Boundary values
- ✅ Format validation
- ✅ Equality and hashCode

**Aggregate Root Checklist:**
- ✅ Factory methods
- ✅ Immutability
- ✅ `from()` reconstruction
- ✅ Method chaining

### Application Layer

**UseCase with MockK:**

```kotlin
class RegisterMemberServiceTest : BehaviorSpec({
    Given("회원 등록 서비스가 주어졌을 때") {
        val memberRepository = mockk<MemberRepository>()
        val service = RegisterMemberService(memberRepository)

        When("회원을 등록하면") {
            every { memberRepository.save(any()) } returns savedMember

            val response = service.execute(command)

            Then("Repository의 save가 호출된다") {
                verify(exactly = 1) { memberRepository.save(any()) }
            }
        }
    }
})
```

### Adapter Layer

**Controller with Facade mock:**

```kotlin
class MemberControllerTest : BehaviorSpec({
    Given("회원이 로그인한 상태에서") {
        val facade = mockk<MemberQueryFacade>()
        val controller = MemberController(facade, ...)

        When("회원 정보 조회를 요청하면") {
            every { facade.getCurrentMember().execute(any()) } returns response

            val result = controller.getCurrentMember(memberId)

            Then("성공 응답이 반환된다") {
                result.statusCode shouldBe HttpStatus.OK
            }
        }
    }
})
```

## Tidy First: Separate Changes

**Never mix Structural and Behavioral changes**

See [tidy-first-principles.md](references/tidy-first-principles.md).

**Structural:** Rename, extract, move (no behavior change)
**Behavioral:** New features, bug fixes (requires tests)

**Rule:** Structural first, then behavioral separately.

## Commands

```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests MemberTest

# Run with coverage
./gradlew test koverHtmlReport

# Verify 60% coverage
./gradlew koverVerify
```

## References

- **[tdd-cycle.md](references/tdd-cycle.md)** - Red → Green → Refactor details
- **[tidy-first-principles.md](references/tidy-first-principles.md)** - Structural vs Behavioral
- **[test-patterns.md](references/test-patterns.md)** - BehaviorSpec, MockK, all layer patterns
