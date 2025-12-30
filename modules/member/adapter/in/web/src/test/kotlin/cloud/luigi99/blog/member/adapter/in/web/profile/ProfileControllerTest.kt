package cloud.luigi99.blog.member.adapter.`in`.web.profile

import cloud.luigi99.blog.member.adapter.`in`.web.profile.dto.ProfileResponse
import cloud.luigi99.blog.member.adapter.`in`.web.profile.dto.UpdateProfileRequest
import cloud.luigi99.blog.member.application.member.port.`in`.command.MemberCommandFacade
import cloud.luigi99.blog.member.application.member.port.`in`.command.UpdateMemberProfileUseCase
import cloud.luigi99.blog.member.application.member.port.`in`.query.GetMemberProfileUseCase
import cloud.luigi99.blog.member.application.member.port.`in`.query.MemberQueryFacade
import cloud.luigi99.blog.member.domain.profile.exception.ProfileException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import java.util.UUID

/**
 * ProfileController 테스트
 *
 * 프로필 REST API 엔드포인트의 요청/응답 처리를 검증합니다.
 */
class ProfileControllerTest :
    BehaviorSpec({

        val memberQueryFacade = mockk<MemberQueryFacade>()
        val memberCommandFacade = mockk<MemberCommandFacade>()
        val controller = ProfileController(memberQueryFacade, memberCommandFacade)

        Given("회원이 프로필을 이미 등록한 상태에서") {
            val memberId = UUID.randomUUID().toString()
            val profileId = UUID.randomUUID().toString()
            val getMemberProfileUseCase = mockk<GetMemberProfileUseCase>()

            every { memberQueryFacade.getMemberProfile() } returns getMemberProfileUseCase

            When("자신의 프로필 조회를 요청하면") {
                val expectedResponse =
                    GetMemberProfileUseCase.Response(
                        memberId = memberId,
                        email = "test@example.com",
                        username = "testuser",
                        profile =
                            GetMemberProfileUseCase.ProfileResponse(
                                profileId = profileId,
                                nickname = "테스터",
                                bio = "안녕하세요",
                                profileImageUrl = "https://example.com/image.jpg",
                                jobTitle = "Backend Developer",
                                techStack = listOf("Kotlin", "Spring Boot"),
                                githubUrl = "https://github.com/testuser",
                                contactEmail = "contact@example.com",
                                websiteUrl = "https://testuser.dev",
                            ),
                    )

                every { getMemberProfileUseCase.execute(any()) } returns expectedResponse

                val response = controller.getProfile(memberId)

                Then("성공 응답과 함께 프로필 정보가 반환되어야 한다") {
                    response.statusCode shouldBe HttpStatus.OK
                    response.body shouldNotBe null
                    response.body?.success shouldBe true
                    response.body
                        ?.data
                        ?.profileId shouldBe profileId
                    response.body
                        ?.data
                        ?.memberId shouldBe memberId
                    response.body
                        ?.data
                        ?.nickname shouldBe "테스터"
                    response.body
                        ?.data
                        ?.bio shouldBe "안녕하세요"
                    response.body
                        ?.data
                        ?.techStack shouldBe listOf("Kotlin", "Spring Boot")
                }
            }
        }

        Given("회원이 아직 프로필을 등록하지 않은 상태에서") {
            val memberId = UUID.randomUUID().toString()
            val getMemberProfileUseCase = mockk<GetMemberProfileUseCase>()

            every { memberQueryFacade.getMemberProfile() } returns getMemberProfileUseCase

            When("프로필 조회를 요청하면") {
                val expectedResponse =
                    GetMemberProfileUseCase.Response(
                        memberId = memberId,
                        email = "noProfile@example.com",
                        username = "noProfileUser",
                        profile = null,
                    )

                every { getMemberProfileUseCase.execute(any()) } returns expectedResponse

                Then("프로필을 찾을 수 없다는 예외가 발생해야 한다") {
                    shouldThrow<ProfileException> {
                        controller.getProfile(memberId)
                    }
                }
            }
        }

        Given("회원이 프로필 정보를 변경하려고 할 때") {
            val memberId = UUID.randomUUID().toString()
            val profileId = UUID.randomUUID().toString()
            val updateMemberProfileUseCase = mockk<UpdateMemberProfileUseCase>()

            every { memberCommandFacade.updateProfile() } returns updateMemberProfileUseCase

            When("새로운 프로필 정보로 업데이트를 요청하면") {
                val request =
                    UpdateProfileRequest(
                        nickname = "새닉네임",
                        bio = "업데이트된 소개",
                        profileImageUrl = "https://example.com/new-image.jpg",
                        jobTitle = "Senior Backend Developer",
                        techStack = listOf("Kotlin", "Spring Boot", "DDD"),
                        githubUrl = "https://github.com/newuser",
                        contactEmail = "new@example.com",
                        websiteUrl = "https://newuser.dev",
                    )

                val expectedResponse =
                    UpdateMemberProfileUseCase.Response(
                        profileId = profileId,
                        nickname = "새닉네임",
                        bio = "업데이트된 소개",
                        profileImageUrl = "https://example.com/new-image.jpg",
                        jobTitle = "Senior Backend Developer",
                        techStack = listOf("Kotlin", "Spring Boot", "DDD"),
                        githubUrl = "https://github.com/newuser",
                        contactEmail = "new@example.com",
                        websiteUrl = "https://newuser.dev",
                    )

                every { updateMemberProfileUseCase.execute(any()) } returns expectedResponse

                val response = controller.updateProfile(memberId, request)

                Then("성공 응답과 함께 변경된 프로필 정보가 반환되어야 한다") {
                    response.statusCode shouldBe HttpStatus.OK
                    response.body shouldNotBe null
                    response.body?.success shouldBe true
                    response.body
                        ?.data
                        ?.profileId shouldBe profileId
                    response.body
                        ?.data
                        ?.nickname shouldBe "새닉네임"
                    response.body
                        ?.data
                        ?.bio shouldBe "업데이트된 소개"
                    response.body
                        ?.data
                        ?.jobTitle shouldBe "Senior Backend Developer"
                    response.body
                        ?.data
                        ?.techStack shouldBe listOf("Kotlin", "Spring Boot", "DDD")
                }
            }
        }

        Given("회원이 닉네임만으로 간단하게 프로필을 구성하려고 할 때") {
            val memberId = UUID.randomUUID().toString()
            val profileId = UUID.randomUUID().toString()
            val updateMemberProfileUseCase = mockk<UpdateMemberProfileUseCase>()

            every { memberCommandFacade.updateProfile() } returns updateMemberProfileUseCase

            When("필수 정보만 입력하고 선택 항목은 비워서 요청하면") {
                val request =
                    UpdateProfileRequest(
                        nickname = "최소닉네임",
                        bio = null,
                        profileImageUrl = null,
                        jobTitle = null,
                        techStack = emptyList(),
                        githubUrl = null,
                        contactEmail = null,
                        websiteUrl = null,
                    )

                val expectedResponse =
                    UpdateMemberProfileUseCase.Response(
                        profileId = profileId,
                        nickname = "최소닉네임",
                        bio = null,
                        profileImageUrl = null,
                        jobTitle = null,
                        techStack = emptyList(),
                        githubUrl = null,
                        contactEmail = null,
                        websiteUrl = null,
                    )

                every { updateMemberProfileUseCase.execute(any()) } returns expectedResponse

                val response = controller.updateProfile(memberId, request)

                Then("필수 정보만 포함된 프로필이 정상적으로 저장되어야 한다") {
                    response.statusCode shouldBe HttpStatus.OK
                    response.body
                        ?.data
                        ?.nickname shouldBe "최소닉네임"
                    response.body
                        ?.data
                        ?.bio shouldBe null
                    response.body
                        ?.data
                        ?.profileImageUrl shouldBe null
                    response.body
                        ?.data
                        ?.techStack shouldBe emptyList()
                }
            }
        }
    })
