package cloud.luigi99.blog.content.post.application.service.query

import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode
import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostContributionsUseCase
import cloud.luigi99.blog.content.post.application.port.out.PostRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import java.time.LocalDate

class GetPostContributionsServiceTest :
    BehaviorSpec({
        Given("잘못된 기여도 조회 파라미터로 조회할 때") {
            val postRepository = mockk<PostRepository>(relaxed = true)
            val service = GetPostContributionsService(postRepository)

            When("from이 to보다 늦으면") {
                Then("INVALID_INPUT BusinessException이 발생한다") {
                    val exception =
                        shouldThrow<BusinessException> {
                            service.execute(
                                GetPostContributionsUseCase.Query(
                                    from = LocalDate.parse("2026-04-24"),
                                    to = LocalDate.parse("2026-04-01"),
                                ),
                            )
                        }
                    exception.errorCode shouldBe ErrorCode.INVALID_INPUT
                }
            }

            When("존재하지 않는 type 값이면") {
                Then("INVALID_INPUT BusinessException이 발생한다") {
                    val exception =
                        shouldThrow<BusinessException> {
                            service.execute(GetPostContributionsUseCase.Query(type = "INVALID"))
                        }
                    exception.errorCode shouldBe ErrorCode.INVALID_INPUT
                }
            }
        }
    })
