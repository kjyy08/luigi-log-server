package cloud.luigi99.blog.common.web

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
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

            then("totalPages가 1로 설정되어야 한다 (totalElements > 0이므로)") {
                pageResponse.totalPages shouldBe 1
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

    given("1-based 페이지 인덱스 처리") {
        `when`("oneBasedPage가 true일 때 Spring Data Page 변환") {
            val content = listOf("item1", "item2")
            val pageable = PageRequest.of(1, 10) // 0-based에서 1페이지
            val springPage = PageImpl(content, pageable, 25)
            val pageResponse = PageResponse.of(springPage, oneBasedPage = true)

            then("1-based 인덱스로 변환되어야 한다") {
                pageResponse.currentPage shouldBe 2 // 0-based 1 -> 1-based 2
                pageResponse.totalElements shouldBe 25
                pageResponse.totalPages shouldBe 3
                pageResponse.first shouldBe false
                pageResponse.last shouldBe false
                pageResponse.hasNext shouldBe true
                pageResponse.hasPrevious shouldBe true
            }
        }

        `when`("oneBasedPage가 true일 때 직접 생성") {
            val content = listOf(1, 2, 3)
            val pageResponse = PageResponse.of(
                content = content,
                totalElements = 50,
                currentPage = 2, // 1-based에서 2페이지
                size = 10,
                oneBasedPage = true
            )

            then("1-based 인덱스로 처리되어야 한다") {
                pageResponse.currentPage shouldBe 2
                pageResponse.totalPages shouldBe 5
                pageResponse.first shouldBe false
                pageResponse.last shouldBe false
                pageResponse.hasNext shouldBe true
                pageResponse.hasPrevious shouldBe true
            }
        }
    }

    given("입력 검증") {
        `when`("음수 totalElements가 주어질 때") {
            then("IllegalArgumentException이 발생해야 한다") {
                shouldThrow<IllegalArgumentException> {
                    PageResponse.of(
                        content = emptyList<String>(),
                        totalElements = -1,
                        currentPage = 0,
                        size = 10
                    )
                }
            }
        }

        `when`("음수 size가 주어질 때") {
            then("IllegalArgumentException이 발생해야 한다") {
                shouldThrow<IllegalArgumentException> {
                    PageResponse.of(
                        content = emptyList<String>(),
                        totalElements = 10,
                        currentPage = 0,
                        size = -1
                    )
                }
            }
        }

        `when`("음수 currentPage가 주어질 때 (0-based)") {
            then("IllegalArgumentException이 발생해야 한다") {
                shouldThrow<IllegalArgumentException> {
                    PageResponse.of(
                        content = emptyList<String>(),
                        totalElements = 10,
                        currentPage = -1,
                        size = 10
                    )
                }
            }
        }

        `when`("0 이하 currentPage가 주어질 때 (1-based)") {
            then("IllegalArgumentException이 발생해야 한다") {
                shouldThrow<IllegalArgumentException> {
                    PageResponse.of(
                        content = emptyList<String>(),
                        totalElements = 10,
                        currentPage = 0,
                        size = 10,
                        oneBasedPage = true
                    )
                }
            }
        }
    }

    given("경계값 처리") {
        `when`("currentPage가 totalPages를 초과할 때") {
            val content = listOf("item")
            val pageResponse = PageResponse.of(
                content = content,
                totalElements = 5,
                currentPage = 10, // 유효 범위를 벗어남
                size = 10
            )

            then("마지막 페이지로 조정되어야 한다") {
                pageResponse.currentPage shouldBe 0 // 마지막 페이지 (0-based)
                pageResponse.totalPages shouldBe 1
                pageResponse.first shouldBe true
                pageResponse.last shouldBe true
                pageResponse.hasNext shouldBe false
                pageResponse.hasPrevious shouldBe false
            }
        }

        `when`("size가 0이고 totalElements가 양수일 때") {
            val content = listOf("item1", "item2")
            val pageResponse = PageResponse.of(
                content = content,
                totalElements = 5,
                currentPage = 0,
                size = 0
            )

            then("totalPages가 1로 설정되어야 한다") {
                pageResponse.totalPages shouldBe 1
                pageResponse.first shouldBe true
                pageResponse.last shouldBe true
                pageResponse.hasNext shouldBe false
                pageResponse.hasPrevious shouldBe false
            }
        }

        `when`("매우 큰 totalElements가 주어질 때") {
            val content = emptyList<String>()
            val pageResponse = PageResponse.of(
                content = content,
                totalElements = Long.MAX_VALUE,
                currentPage = 0,
                size = 1
            )

            then("오버플로우 없이 처리되어야 한다") {
                pageResponse.totalPages shouldBe Int.MAX_VALUE
                pageResponse.totalElements shouldBe Long.MAX_VALUE
            }
        }
    }

    given("빈 PageResponse 생성") {
        `when`("empty() 메서드를 사용할 때 (0-based)") {
            val pageResponse = PageResponse.empty<String>(size = 10)

            then("올바른 빈 페이지가 생성되어야 한다") {
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

        `when`("empty() 메서드를 사용할 때 (1-based)") {
            val pageResponse = PageResponse.empty<String>(size = 10, oneBasedPage = true)

            then("1-based 인덱스로 빈 페이지가 생성되어야 한다") {
                pageResponse.content shouldBe emptyList()
                pageResponse.totalElements shouldBe 0
                pageResponse.totalPages shouldBe 0
                pageResponse.currentPage shouldBe 1
                pageResponse.size shouldBe 10
                pageResponse.first shouldBe true
                pageResponse.last shouldBe true
                pageResponse.hasNext shouldBe false
                pageResponse.hasPrevious shouldBe false
            }
        }
    }

    given("방어적 복사") {
        `when`("원본 리스트를 수정할 때") {
            val originalList = mutableListOf("item1", "item2")
            val pageResponse = PageResponse.of(
                content = originalList,
                totalElements = 2,
                currentPage = 0,
                size = 10
            )

            originalList.add("item3") // 원본 수정

            then("PageResponse의 content는 영향받지 않아야 한다") {
                pageResponse.content shouldBe listOf("item1", "item2")
                pageResponse.content.size shouldBe 2
            }
        }

        `when`("Spring Data Page의 content를 수정할 때") {
            val originalList = mutableListOf("item1", "item2")
            val pageable = PageRequest.of(0, 10)
            val springPage = PageImpl(originalList, pageable, 2)
            val pageResponse = PageResponse.of(springPage)

            originalList.clear() // 원본 수정

            then("PageResponse의 content는 영향받지 않아야 한다") {
                pageResponse.content shouldBe listOf("item1", "item2")
                pageResponse.content.size shouldBe 2
            }
        }
    }
})