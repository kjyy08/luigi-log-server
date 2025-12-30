# Test Patterns

## Framework: Kotest BehaviorSpec

**ALWAYS use BehaviorSpec with Korean Given-When-Then:**

```kotlin
class MemberTest : BehaviorSpec({
    Given("유효한 이메일과 사용자 이름이 주어졌을 때") {
        val email = Email("user@example.com")
        val username = Username("john_doe")

        When("회원을 등록하면") {
            val member = Member.register(email, username)

            Then("회원 ID가 생성된다") {
                member.entityId shouldNotBe null
            }

            Then("이메일이 올바르게 설정된다") {
                member.email shouldBe email
            }
        }
    }
})
```

## Kotest Matchers

```kotlin
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.assertions.throwables.shouldThrow

// Equality
member.email shouldBe email
member.email shouldNotBe null

// String
exception.message shouldContain "Email cannot be blank"

// Exception
shouldThrow<IllegalArgumentException> {
    Email("")
}
```

## MockK Setup

```kotlin
import io.mockk.*

class MemberServiceTest : BehaviorSpec({
    beforeTest {
        // Mock EventManager for domain events
        mockkObject(EventManager)
        every { EventManager.eventContextManager } returns mockk(relaxed = true)
    }

    Given("회원 서비스가 주어졌을 때") {
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

## Domain Layer Tests

**Pure domain logic, no framework dependencies**

### Value Object Tests

**Checklist:**
- ✅ Valid value creation
- ✅ Blank/empty string validation
- ✅ Boundary values (min/max length)
- ✅ Format validation (email, URL)
- ✅ Equality and hashCode
- ✅ toString()
- ✅ Multiple valid cases

**Example: EmailTest.kt**
```kotlin
class EmailTest : BehaviorSpec({

    Given("유효한 이메일 주소가 주어졌을 때") {
        val validEmail = "user@example.com"

        When("Email 객체를 생성하면") {
            val email = Email(validEmail)

            Then("이메일 값이 올바르게 저장된다") {
                email.value shouldBe validEmail
            }
        }
    }

    Given("빈 문자열이 주어졌을 때") {
        val emptyEmail = ""

        When("Email 객체를 생성하려고 하면") {
            val exception = shouldThrow<IllegalArgumentException> {
                Email(emptyEmail)
            }

            Then("예외가 발생한다") {
                exception.message shouldContain "Email cannot be blank"
            }
        }
    }

    Given("다양한 형식의 유효한 이메일이 주어졌을 때") {
        val validEmails = listOf(
            "simple@example.com",
            "user.name@example.com",
            "user+tag@example.co.kr",
        )

        validEmails.forEach { validEmail ->
            When("이메일 '$validEmail'로 Email 객체를 생성하면") {
                val email = Email(validEmail)

                Then("정상적으로 생성된다") {
                    email shouldNotBe null
                    email.value shouldBe validEmail
                }
            }
        }
    }

    Given("동일한 이메일 주소로 생성된 두 Email 객체가 있을 때") {
        val email1 = Email("user@example.com")
        val email2 = Email("user@example.com")

        Then("두 객체는 동일하다") {
            email1 shouldBe email2
        }

        Then("해시코드도 동일하다") {
            email1.hashCode() shouldBe email2.hashCode()
        }
    }
})
```

### Aggregate Root Tests

**Checklist:**
- ✅ Factory methods (`register()`)
- ✅ Business logic methods
- ✅ Immutability (original unchanged, new instance returned)
- ✅ Persistence reconstruction (`from()`)
- ✅ Method chaining
- ✅ Entity ID preservation

**Example: MemberTest.kt**
```kotlin
class MemberTest : BehaviorSpec({

    beforeTest {
        mockkObject(EventManager)
        every { EventManager.eventContextManager } returns mockk(relaxed = true)
    }

    Given("유효한 이메일과 사용자 이름이 주어졌을 때") {
        val email = Email("user@example.com")
        val username = Username("john_doe")

        When("회원을 등록하면") {
            val member = Member.register(email, username)

            Then("회원 ID가 생성된다") {
                member.entityId shouldNotBe null
            }

            Then("이메일이 올바르게 설정된다") {
                member.email shouldBe email
            }
        }
    }

    Given("회원의 불변성을 테스트할 때") {
        val originalMember = Member.register(
            Email("user@example.com"),
            Username("john_doe")
        )
        val originalUsername = originalMember.username

        When("사용자 이름을 변경하면") {
            val updatedMember = originalMember.updateUsername(Username("jane_doe"))

            Then("원본 회원 객체는 변경되지 않는다") {
                originalMember.username shouldBe originalUsername
                originalMember.username.value shouldBe "john_doe"
            }

            Then("새로운 회원 객체가 반환된다") {
                updatedMember.username.value shouldBe "jane_doe"
            }

            Then("엔티티 ID는 동일하게 유지된다") {
                updatedMember.entityId shouldBe originalMember.entityId
            }
        }
    }

    Given("영속성 계층에서 로드할 데이터가 있을 때") {
        val memberId = MemberId.generate()
        val email = Email("user@example.com")
        val username = Username("john_doe")
        val createdAt = LocalDateTime.now().minusDays(7)
        val updatedAt = LocalDateTime.now().minusDays(1)

        When("from()을 사용하여 Member를 재구성하면") {
            val member = Member.from(
                entityId = memberId,
                email = email,
                username = username,
                profile = null,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )

            Then("모든 속성이 올바르게 설정된다") {
                member.entityId shouldBe memberId
                member.email shouldBe email
                member.username shouldBe username
                member.createdAt shouldBe createdAt
                member.updatedAt shouldBe updatedAt
            }
        }
    }
})
```

## Application Layer Tests

**UseCase tests with MockK Repository**

```kotlin
class RegisterMemberServiceTest : BehaviorSpec({

    Given("회원 등록 서비스가 주어졌을 때") {
        val memberRepository = mockk<MemberRepository>()
        val service = RegisterMemberService(memberRepository)

        val command = RegisterMemberUseCase.Command(
            email = "user@example.com",
            username = "john_doe"
        )

        val savedMember = Member.register(
            Email("user@example.com"),
            Username("john_doe")
        )

        When("유효한 이메일과 사용자 이름으로 회원을 등록하면") {
            every { memberRepository.save(any()) } returns savedMember

            val response = service.execute(command)

            Then("Repository의 save가 호출된다") {
                verify(exactly = 1) { memberRepository.save(any()) }
            }

            Then("회원 ID가 반환된다") {
                response.memberId shouldNotBe null
            }

            Then("이메일이 올바르게 반환된다") {
                response.email shouldBe "user@example.com"
            }
        }
    }

    Given("존재하지 않는 회원 ID로 조회를 시도할 때") {
        val memberRepository = mockk<MemberRepository>()
        val service = GetCurrentMemberService(memberRepository)

        val query = GetCurrentMemberUseCase.Query(
            memberId = "non-existent-id"
        )

        When("존재하지 않는 회원 ID로 조회하면") {
            every { memberRepository.findById(any()) } returns null

            Then("MemberNotFoundException이 발생한다") {
                shouldThrow<MemberNotFoundException> {
                    service.execute(query)
                }
            }
        }
    }
})
```

## Adapter/In/Web Layer Tests

**Controller tests with Facade mocking**

```kotlin
class MemberControllerTest : BehaviorSpec({

    Given("회원이 로그인한 상태에서") {
        val memberQueryFacade = mockk<MemberQueryFacade>()
        val memberCommandFacade = mockk<MemberCommandFacade>()
        val controller = MemberController(memberQueryFacade, memberCommandFacade)

        val memberId = "123e4567-e89b-12d3-a456-426614174000"
        val useCase = mockk<GetCurrentMemberUseCase>()

        val response = GetCurrentMemberUseCase.Response(
            memberId = memberId,
            email = "user@example.com",
            username = "john_doe"
        )

        When("자신의 회원 정보 조회를 요청하면") {
            every { memberQueryFacade.getCurrentMember() } returns useCase
            every { useCase.execute(any()) } returns response

            val result = controller.getCurrentMember(memberId)

            Then("성공 응답과 함께 회원 정보가 반환되어야 한다") {
                result.statusCode shouldBe HttpStatus.OK
                result.body?.success shouldBe true
                result.body?.data?.memberId shouldBe memberId
                result.body?.data?.email shouldBe "user@example.com"
            }
        }
    }
})
```

## Adapter/Out/JPA Layer Tests

### Mapper Tests

```kotlin
class MemberMapperTest : BehaviorSpec({

    Given("회원 도메인 모델이 주어졌을 때") {
        val member = Member.register(
            Email("user@example.com"),
            Username("john_doe")
        )

        When("JPA 엔티티로 변환하면") {
            val entity = MemberMapper.toEntity(member)

            Then("모든 필드가 정확하게 매핑되어야 한다") {
                entity.id shouldBe member.entityId.value
                entity.email shouldBe member.email.value
                entity.username shouldBe member.username.value
            }
        }
    }

    Given("도메인 모델을 JPA 엔티티로 변환한 후") {
        val member = Member.register(
            Email("user@example.com"),
            Username("john_doe")
        )

        When("다시 도메인 모델로 변환하면") {
            val entity = MemberMapper.toEntity(member)
            val converted = MemberMapper.toDomain(entity)

            Then("원본 데이터가 손실 없이 복원되어야 한다") {
                converted.entityId shouldBe member.entityId
                converted.email shouldBe member.email
                converted.username shouldBe member.username
            }
        }
    }
})
```

### Repository Adapter Tests

```kotlin
class MemberRepositoryAdapterTest : BehaviorSpec({

    Given("새로운 회원을 등록하려고 할 때") {
        val jpaRepository = mockk<MemberJpaRepository>()
        val eventContextManager = mockk<EventContextManager>()
        val domainEventPublisher = mockk<DomainEventPublisher>()

        val adapter = MemberRepositoryAdapter(
            jpaRepository,
            eventContextManager,
            domainEventPublisher
        )

        val member = Member.register(
            Email("user@example.com"),
            Username("john_doe")
        )

        When("회원 정보를 저장하면") {
            val savedEntity = MemberMapper.toEntity(member)
            every { jpaRepository.save(any()) } returns savedEntity
            every { eventContextManager.getDomainEventsAndClear() } returns listOf(
                MemberRegisteredEvent(member.entityId, member.email)
            )
            every { domainEventPublisher.publish(any()) } just Runs

            val saved = adapter.save(member)

            Then("저장된 회원 정보가 반환되어야 한다") {
                saved.entityId shouldBe member.entityId
                saved.email shouldBe member.email
            }

            Then("도메인 이벤트가 발행되어야 한다") {
                verify(exactly = 1) {
                    domainEventPublisher.publish(match { it is MemberRegisteredEvent })
                }
            }
        }
    }
})
```

## Test Commands

```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :modules:member:domain:test

# Run specific test class
./gradlew test --tests MemberTest

# Run with coverage
./gradlew test koverHtmlReport
```
