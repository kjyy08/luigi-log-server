package cloud.luigi99.blog.content.domain.post.model

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.domain.post.vo.Body
import cloud.luigi99.blog.content.domain.post.vo.ContentType
import cloud.luigi99.blog.content.domain.post.vo.PostId
import cloud.luigi99.blog.content.domain.post.vo.PostStatus
import cloud.luigi99.blog.content.domain.post.vo.Slug
import cloud.luigi99.blog.content.domain.post.vo.Title
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import java.time.LocalDateTime

/**
 * Post Aggregate Root 테스트
 *
 * Post의 핵심 비즈니스 로직을 검증합니다.
 */
class PostTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }
        Given("유효한 memberId, 제목, slug, 본문이 주어졌을 때") {
            val memberId = MemberId.generate()
            val title = Title("테스트 블로그 글")
            val slug = Slug("test-blog-post")
            val body = Body("# 제목\n본문 내용")

            When("BLOG 타입으로 Post를 생성하면") {
                val post = Post.create(memberId, title, slug, body, ContentType.BLOG)

                Then("DRAFT 상태로 생성된다") {
                    post.status shouldBe PostStatus.DRAFT
                }

                Then("빈 태그 Set을 가진다") {
                    post.tags.shouldBeEmpty()
                }

                Then("PostId가 생성된다") {
                    post.entityId shouldNotBe null
                }

                Then("memberId가 설정된다") {
                    post.memberId shouldBe memberId
                }

                Then("입력한 값들이 설정된다") {
                    post.title shouldBe title
                    post.slug shouldBe slug
                    post.body shouldBe body
                    post.type shouldBe ContentType.BLOG
                }
            }

            When("PORTFOLIO 타입으로 Post를 생성하면") {
                val post = Post.create(memberId, title, slug, body, ContentType.PORTFOLIO)

                Then("타입이 PORTFOLIO로 설정된다") {
                    post.type shouldBe ContentType.PORTFOLIO
                }
            }
        }

        Given("Post 작성자 검증 시") {
            val memberId = MemberId.generate()
            val otherMemberId = MemberId.generate()
            val post =
                Post.create(
                    memberId,
                    Title("소유권 테스트"),
                    Slug("ownership-test"),
                    Body("소유권 테스트 내용"),
                    ContentType.BLOG,
                )

            When("작성자 본인이 권한을 확인하면") {
                val isOwner = post.isOwner(memberId)

                Then("true를 반환한다") {
                    isOwner shouldBe true
                }
            }

            When("다른 사용자가 권한을 확인하면") {
                val isOwner = post.isOwner(otherMemberId)

                Then("false를 반환한다") {
                    isOwner shouldBe false
                }
            }
        }

        Given("DRAFT 상태의 Post가 주어졌을 때") {
            val post =
                Post.create(
                    MemberId.generate(),
                    Title("초안 글"),
                    Slug("draft-post"),
                    Body("초안 내용"),
                    ContentType.BLOG,
                )
            post.clearEvents()

            When("발행하면") {
                val published = post.publish()

                Then("상태가 PUBLISHED로 변경된다") {
                    published.status shouldBe PostStatus.PUBLISHED
                }

                Then("새로운 인스턴스가 반환된다") {
                    published shouldNotBeSameInstanceAs post
                }
            }
        }

        Given("PUBLISHED 상태의 Post가 주어졌을 때") {
            val post =
                Post
                    .create(
                        MemberId.generate(),
                        Title("발행된 글"),
                        Slug("published-post"),
                        Body("발행된 내용"),
                        ContentType.BLOG,
                    ).publish()
            post.clearEvents()

            When("아카이빙하면") {
                val archived = post.archive()

                Then("상태가 ARCHIVED로 변경된다") {
                    archived.status shouldBe PostStatus.ARCHIVED
                }

                Then("새로운 인스턴스가 반환된다") {
                    archived shouldNotBeSameInstanceAs post
                }
            }
        }

        Given("Post가 주어졌을 때") {
            val originalTitle = Title("원본 제목")
            val originalBody = Body("원본 내용")
            val post =
                Post.create(
                    MemberId.generate(),
                    originalTitle,
                    Slug("original-post"),
                    originalBody,
                    ContentType.BLOG,
                )

            When("제목과 본문을 수정하면") {
                val newTitle = Title("수정된 제목")
                val newBody = Body("수정된 내용")
                val updated = post.update(newTitle, newBody)

                Then("제목과 본문이 변경된다") {
                    updated.title shouldBe newTitle
                    updated.body shouldBe newBody
                }

                Then("새로운 인스턴스가 반환된다") {
                    updated shouldNotBeSameInstanceAs post
                }

                Then("slug와 타입은 유지된다") {
                    updated.slug shouldBe post.slug
                    updated.type shouldBe post.type
                }
            }
        }

        Given("태그가 없는 Post가 주어졌을 때") {
            val post =
                Post.create(
                    MemberId.generate(),
                    Title("태그 테스트"),
                    Slug("tag-test"),
                    Body("태그 테스트 내용"),
                    ContentType.BLOG,
                )

            When("태그를 추가하면") {
                val withTag = post.addTag("kotlin")

                Then("태그가 포함된다") {
                    withTag.tags shouldContain "kotlin"
                }

                Then("새로운 인스턴스가 반환된다") {
                    withTag shouldNotBeSameInstanceAs post
                }
            }

            When("여러 태그를 추가하면") {
                val withTags =
                    post
                        .addTag("kotlin")
                        .addTag("spring")
                        .addTag("jpa")

                Then("모든 태그가 포함된다") {
                    withTags.tags shouldHaveSize 3
                    withTags.tags shouldContain "kotlin"
                    withTags.tags shouldContain "spring"
                    withTags.tags shouldContain "jpa"
                }
            }
        }

        Given("중복 태그를 추가하려는 경우") {
            val post =
                Post
                    .create(
                        MemberId.generate(),
                        Title("중복 태그 테스트"),
                        Slug("duplicate-tag-test"),
                        Body("내용"),
                        ContentType.BLOG,
                    ).addTag("kotlin")

            When("동일한 태그를 다시 추가하면") {
                val withDuplicateTag = post.addTag("kotlin")

                Then("태그가 중복되지 않는다") {
                    withDuplicateTag.tags shouldHaveSize 1
                    withDuplicateTag.tags shouldContain "kotlin"
                }
            }
        }

        Given("태그가 있는 Post가 주어졌을 때") {
            val post =
                Post
                    .create(
                        MemberId.generate(),
                        Title("태그 제거 테스트"),
                        Slug("remove-tag-test"),
                        Body("내용"),
                        ContentType.BLOG,
                    ).addTag("kotlin")
                    .addTag("spring")

            When("태그를 제거하면") {
                val removed = post.removeTag("kotlin")

                Then("해당 태그가 제거된다") {
                    removed.tags shouldNotContain "kotlin"
                    removed.tags shouldContain "spring"
                }

                Then("새로운 인스턴스가 반환된다") {
                    removed shouldNotBeSameInstanceAs post
                }
            }

            When("존재하지 않는 태그를 제거하면") {
                val removed = post.removeTag("java")

                Then("변경 없이 반환된다") {
                    removed.tags shouldHaveSize 2
                }
            }
        }

        Given("영속성에서 로드된 데이터가 주어졌을 때") {
            val entityId = PostId.generate()
            val memberId = MemberId.generate()
            val title = Title("재구성 테스트")
            val slug = Slug("reconstruction-test")
            val body = Body("재구성 내용")
            val tags = setOf("kotlin", "tdd")
            val metadata =
                LocalDateTime
                    .now()

            When("from()으로 Post를 재구성하면") {
                val post =
                    Post.from(
                        entityId = entityId,
                        memberId = memberId,
                        title = title,
                        slug = slug,
                        body = body,
                        type = ContentType.BLOG,
                        status = PostStatus.PUBLISHED,
                        tags = tags,
                        createdAt = metadata,
                        updatedAt = metadata,
                    )

                Then("모든 값이 복원된다") {
                    post.memberId shouldBe memberId
                    post.title shouldBe title
                    post.slug shouldBe slug
                    post.body shouldBe body
                    post.type shouldBe ContentType.BLOG
                    post.status shouldBe PostStatus.PUBLISHED
                    post.tags shouldBe tags
                    post.createdAt.shouldNotBeNull()
                    post.updatedAt.shouldNotBeNull()
                }

                Then("이벤트가 발행되지 않는다") {
                    post.getEvents().shouldBeEmpty()
                }
            }
        }

        Given("작성자가 게시글을 삭제하려는 상황에서") {
            val post =
                Post.create(
                    MemberId.generate(),
                    Title("삭제할 글"),
                    Slug("delete-post"),
                    Body("내용"),
                    ContentType.BLOG,
                )
            post.clearEvents()

            When("삭제를 수행하면") {
                val deleted = post.delete()

                Then("기존 객체와 다른 새로운 인스턴스가 반환된다") {
                    deleted shouldNotBeSameInstanceAs post
                }
            }
        }
    })
