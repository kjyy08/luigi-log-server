# Test Patterns

## Framework: Kotest BehaviorSpec

**ALWAYS use BehaviorSpec with Korean Given-When-Then:**

```kotlin
class MemberTest : BehaviorSpec({
    Given("신규 사용자가 가입을 위해 유효한 이메일과 이름을 입력했을 때") {
        val email = Email("user@example.com")
        val username = Username("john_doe")

        When("회원 가입을 요청하면") {
            val member = Member.register(email, username)

            Then("회원 가입이 완료되어 식별자가 부여된다") {
                member.entityId shouldNotBe null
            }

            Then("요청한 이메일로 회원 정보가 설정된다") {
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

    Given("신규 회원 가입 요청을 처리하는 상황에서") {
        val memberRepository = mockk<MemberRepository>()
        val service = RegisterMemberService(memberRepository)

        When("회원 가입을 진행하면") {
            every { memberRepository.save(any()) } returns savedMember

            val response = service.execute(command)

            Then("회원 정보가 시스템에 저장된다") {
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

    Given("사용자가 연락처로 사용할 유효한 이메일을 입력했을 때") {
        val validEmail = "user@example.com"

        When("시스템 내에서 사용할 이메일 정보를 생성하면") {
            val email = Email(validEmail)

            Then("이메일 값이 올바르게 저장된다") {
                email.value shouldBe validEmail
            }
        }
    }

    Given("필수 입력 항목인 이메일을 입력하지 않았을 때") {
        val emptyEmail = ""

        When("이메일 정보 생성을 시도하면") {
            val exception = shouldThrow<IllegalArgumentException> {
                Email(emptyEmail)
            }

            Then("유효하지 않은 형식이므로 생성이 거절된다") {
                exception.message shouldContain "Email cannot be blank"
            }
        }
    }

    Given("사용자가 다양한 형식의 유효한 이메일 주소를 입력했을 때") {
        val validEmails = listOf(
            "simple@example.com",
            "user.name@example.com",
            "user+tag@example.co.kr",
        )

        validEmails.forEach { validEmail ->
            When("이메일 '$validEmail'로 이메일 정보를 생성하면") {
                val email = Email(validEmail)

                Then("정상적으로 생성된다") {
                    email shouldNotBe null
                    email.value shouldBe validEmail
                }
            }
        }
    }

    Given("시스템 내에서 동일한 이메일 주소를 가리키는 두 데이터가 있을 때") {
        val email1 = Email("user@example.com")
        val email2 = Email("user@example.com")

        Then("두 데이터는 논리적으로 동일한 것으로 취급한다") {
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

    Given("신규 가입을 위해 유효한 이메일과 이름을 제출했을 때") {
        val email = Email("user@example.com")
        val username = Username("john_doe")

        When("회원 가입을 요청하면") {
            val member = Member.register(email, username)

            Then("회원 가입이 완료되어 식별자가 부여된다") {
                member.entityId shouldNotBe null
            }

            Then("요청한 이메일로 회원 정보가 설정된다") {
                member.email shouldBe email
            }
        }
    }

    Given("기존 회원이 자신의 사용자 이름을 변경하려고 할 때") {
        val originalMember = Member.register(
            Email("user@example.com"),
            Username("john_doe")
        )
        val originalUsername = originalMember.username

        When("새로운 이름으로 변경을 요청하면") {
            val updatedMember = originalMember.updateUsername(Username("jane_doe"))

            Then("원본 회원 정보는 변경되지 않고 보존된다") {
                originalMember.username shouldBe originalUsername
                originalMember.username.value shouldBe "john_doe"
            }

            Then("변경된 이름을 가진 새로운 회원 정보가 반환된다") {
                updatedMember.username.value shouldBe "jane_doe"
            }

            Then("회원 식별자는 동일하게 유지된다") {
                updatedMember.entityId shouldBe originalMember.entityId
            }
        }
    }

    Given("과거에 가입한 회원의 정보를 데이터베이스에서 불러왔을 때") {
        val memberId = MemberId.generate()
        val email = Email("user@example.com")
        val username = Username("john_doe")
        val createdAt = LocalDateTime.now().minusDays(7)
        val updatedAt = LocalDateTime.now().minusDays(1)

        When("비즈니스 객체로 재구성하면") {
            val member = Member.from(
                entityId = memberId,
                email = email,
                username = username,
                profile = null,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )

            Then("모든 속성이 저장된 상태 그대로 복원된다") {
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

    Given("방문자가 회원 가입 양식을 제출했을 때") {
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

        When("가입 처리를 진행하면") {
            every { memberRepository.save(any()) } returns savedMember

            val response = service.execute(command)

            Then("회원 정보가 시스템에 영구 저장된다") {
                verify(exactly = 1) { memberRepository.save(any()) }
            }

            Then("생성된 회원 ID가 반환된다") {
                response.memberId shouldNotBe null
            }

            Then("등록된 이메일이 반환된다") {
                response.email shouldBe "user@example.com"
            }
        }
    }

    Given("삭제되었거나 존재하지 않는 회원의 프로필을 조회하려 할 때") {
        val memberRepository = mockk<MemberRepository>()
        val service = GetCurrentMemberService(memberRepository)

        val query = GetCurrentMemberUseCase.Query(
            memberId = "non-existent-id"
        )

        When("회원 정보를 조회하면") {
            every { memberRepository.findById(any()) } returns null

            Then("존재하지 않는 회원이므로 조회가 거절된다") {
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

    Given("로그인한 사용자가 자신의 프로필 정보를 요청할 때") {
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

        When("회원 정보 조회를 호출하면") {
            every { memberQueryFacade.getCurrentMember() } returns useCase
            every { useCase.execute(any()) } returns response

            val result = controller.getCurrentMember(memberId)

            Then("요청한 회원의 상세 정보를 확인할 수 있다") {
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

    Given("비즈니스 로직에서 처리된 회원 정보를 데이터베이스에 저장하려 할 때") {
        val member = Member.register(
            Email("user@example.com"),
            Username("john_doe")
        )

        When("데이터베이스 엔티티로 변환하면") {
            val entity = MemberMapper.toEntity(member)

            Then("비즈니스 데이터가 영속성 모델로 정확히 매핑된다") {
                entity.id shouldBe member.entityId.value
                entity.email shouldBe member.email.value
                entity.username shouldBe member.username.value
            }
        }
    }

    Given("데이터베이스에 저장된 회원 정보를 조회하여 비즈니스 객체로 변환할 때") {
        val member = Member.register(
            Email("user@example.com"),
            Username("john_doe")
        )

        When("비즈니스 모델로 변환하면") {
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

    Given("신규 회원 정보가 생성되어 영구 저장이 필요할 때") {
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

        When("저장소를 통해 저장을 요청하면") {
            val savedEntity = MemberMapper.toEntity(member)
            every { jpaRepository.save(any()) } returns savedEntity
            every { eventContextManager.getDomainEventsAndClear() } returns listOf(
                MemberRegisteredEvent(member.entityId, member.email)
            )
            every { domainEventPublisher.publish(any()) } just Runs

            val saved = adapter.save(member)

            Then("회원 정보가 정상적으로 저장되고 반환된다") {
                saved.entityId shouldBe member.entityId
                saved.email shouldBe member.email
            }

            Then("회원 가입 이벤트가 시스템에 전파된다") {
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
