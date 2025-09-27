package cloud.luigi99.blog.common.web

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class PageResponseTest : BehaviorSpec({

    given("Spring Data Page로부터 PageResponse 생성") {
        `when`("첫 번째 페이지의 Page 객체가 주어질 때") {
            val content = listOf("item1", "item2", "item3")
            val pageable = PageRequest.of(0, 10)
            val springPage = PageImpl(content, pageable, 25)
            val pageResponse = PageResponse.of(springPage)

            then("올바른 페이지 정보가 설정되어야 한다") {
                pageResponse.content shouldBe content
                pageResponse.totalElements shouldBe 25
                pageResponse.totalPages shouldBe 3
                pageResponse.currentPage shouldBe 0
                pageResponse.size shouldBe 10
                pageResponse.first shouldBe true
                pageResponse.last shouldBe false
                pageResponse.hasNext shouldBe true
                pageResponse.hasPrevious shouldBe false
            }
        }

        `when`("마지막 페이지의 Page 객체가 주어질 때") {
            val content = listOf("item1", "item2")
            val pageable = PageRequest.of(2, 10)
            val springPage = PageImpl(content, pageable, 22)
            val pageResponse = PageResponse.of(springPage)

            then("마지막 페이지 정보가 올바르게 설정되어야 한다") {
                pageResponse.content shouldBe content
                pageResponse.totalElements shouldBe 22
                pageResponse.totalPages shouldBe 3
                pageResponse.currentPage shouldBe 2
                pageResponse.size shouldBe 10
                pageResponse.first shouldBe false
                pageResponse.last shouldBe true
                pageResponse.hasNext shouldBe false
                pageResponse.hasPrevious shouldBe true
            }
        }

        `when`("빈 페이지가 주어질 때") {
            val content = emptyList<String>()
            val pageable = PageRequest.of(0, 10)
            val springPage = PageImpl(content, pageable, 0)
            val pageResponse = PageResponse.of(springPage)

            then("빈 페이지 정보가 설정되어야 한다") {
                pageResponse.content shouldBe emptyList()
                pageResponse.totalElements shouldBe 0
                pageResponse.totalPages shouldBe 0
                pageResponse.currentPage shouldBe 0
                pageResponse.size shouldBe 10
                pageResponse.first shouldBe true
                pageResponse.last shouldBe true
                pageResponse.hasNext shouldBe false
                pageResponse.hasPrevious shouldBe false
            }
        }
    }

    given("직접 매개변수로 PageResponse 생성") {
        `when`("유효한 매개변수들이 주어질 때") {
            val content = listOf(1, 2, 3, 4, 5)
            val totalElements = 50L
            val currentPage = 1
            val size = 10
            val pageResponse = PageResponse.of(content, totalElements, currentPage, size)

            then("올바른 페이지 정보가 계산되어야 한다") {
                pageResponse.content shouldBe content
                pageResponse.totalElements shouldBe totalElements
                pageResponse.totalPages shouldBe 5
                pageResponse.currentPage shouldBe currentPage
                pageResponse.size shouldBe size
                pageResponse.first shouldBe false
                pageResponse.last shouldBe false
                pageResponse.hasNext shouldBe true
                pageResponse.hasPrevious shouldBe true
            }
        }

        `when`("size가 0인 경우") {
            val content = emptyList<String>()
            val totalElements = 10L
            val currentPage = 0
            val size = 0
            val pageResponse = PageResponse.of(content, totalElements, currentPage, size)

            then("totalPages가 0으로 설정되어야 한다") {
                pageResponse.totalPages shouldBe 0
                pageResponse.first shouldBe true
                pageResponse.last shouldBe true
                pageResponse.hasNext shouldBe false
                pageResponse.hasPrevious shouldBe false
            }
        }

        `when`("단일 페이지만 있는 경우") {
            val content = listOf("only", "item")
            val totalElements = 2L
            val currentPage = 0
            val size = 10
            val pageResponse = PageResponse.of(content, totalElements, currentPage, size)

            then("첫 번째이자 마지막 페이지로 설정되어야 한다") {
                pageResponse.totalPages shouldBe 1
                pageResponse.first shouldBe true
                pageResponse.last shouldBe true
                pageResponse.hasNext shouldBe false
                pageResponse.hasPrevious shouldBe false
            }
        }
    }
})