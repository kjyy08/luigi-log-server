package cloud.luigi99.blog.content.guestbook.adapter.`in`.web

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.content.guestbook.adapter.`in`.web.dto.AuthorResponse
import cloud.luigi99.blog.content.guestbook.adapter.`in`.web.dto.CreateGuestbookRequest
import cloud.luigi99.blog.content.guestbook.adapter.`in`.web.dto.GuestbookResponse
import cloud.luigi99.blog.content.guestbook.adapter.`in`.web.dto.ModifyGuestbookRequest
import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.CreateGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.DeleteGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.GuestbookCommandFacade
import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.ModifyGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.`in`.query.GetGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.`in`.query.GuestbookQueryFacade
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

/**
 * 방명록 컨트롤러
 *
 * 방명록 작성, 수정, 삭제, 조회 등 방명록 관련 요청을 처리합니다.
 */
@RestController
@RequestMapping("/api/v1/guestbooks")
class GuestbookController(
    private val guestbookCommandFacade: GuestbookCommandFacade,
    private val guestbookQueryFacade: GuestbookQueryFacade,
) : GuestbookApi {
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    override fun createGuestbook(
        @AuthenticationPrincipal memberId: String,
        @RequestBody request: CreateGuestbookRequest,
    ): ResponseEntity<CommonResponse<GuestbookResponse>> {
        log.info { "Creating guestbook entry by member: $memberId" }

        val command =
            CreateGuestbookUseCase.Command(
                authorId = memberId,
                content = request.content,
            )

        val response = guestbookCommandFacade.createGuestbook().execute(command)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            CommonResponse.success(
                GuestbookResponse(
                    guestbookId = response.guestbookId,
                    author =
                        AuthorResponse(
                            memberId = response.author.memberId,
                            nickname = response.author.nickname,
                            profileImageUrl = response.author.profileImageUrl,
                            username = response.author.username,
                        ),
                    content = response.content,
                    createdAt = "",
                    updatedAt = "",
                ),
            ),
        )
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{guestbookId}")
    override fun modifyGuestbook(
        @AuthenticationPrincipal memberId: String,
        @PathVariable guestbookId: String,
        @RequestBody request: ModifyGuestbookRequest,
    ): ResponseEntity<CommonResponse<GuestbookResponse>> {
        log.info { "Modifying guestbook: $guestbookId by member: $memberId" }

        val command =
            ModifyGuestbookUseCase.Command(
                guestbookId = guestbookId,
                requesterId = memberId,
                content = request.content,
            )

        guestbookCommandFacade.modifyGuestbook().execute(command)

        val query = GetGuestbookUseCase.Query(guestbookId)
        val getResponse = guestbookQueryFacade.getGuestbook().execute(query)

        return ResponseEntity.ok(
            CommonResponse.success(
                GuestbookResponse(
                    guestbookId = getResponse.guestbookId,
                    author =
                        AuthorResponse(
                            memberId = getResponse.author.memberId,
                            nickname = getResponse.author.nickname,
                            profileImageUrl = getResponse.author.profileImageUrl,
                            username = getResponse.author.username,
                        ),
                    content = getResponse.content,
                    createdAt = getResponse.createdAt,
                    updatedAt = getResponse.updatedAt,
                ),
            ),
        )
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{guestbookId}")
    override fun deleteGuestbook(
        @AuthenticationPrincipal memberId: String,
        @PathVariable guestbookId: String,
    ): ResponseEntity<Unit> {
        log.info { "Deleting guestbook: $guestbookId by member: $memberId" }

        val command =
            DeleteGuestbookUseCase.Command(
                guestbookId = guestbookId,
                requesterId = memberId,
            )

        guestbookCommandFacade.deleteGuestbook().execute(command)

        return ResponseEntity.noContent().build()
    }

    @GetMapping
    override fun getGuestbooks(): ResponseEntity<CommonResponse<List<GuestbookResponse>>> {
        log.info { "Getting all guestbooks" }

        val responses = guestbookQueryFacade.getGuestbookList().execute()

        return ResponseEntity.ok(
            CommonResponse.success(
                responses.map { response ->
                    GuestbookResponse(
                        guestbookId = response.guestbookId,
                        author =
                            AuthorResponse(
                                memberId = response.author.memberId,
                                nickname = response.author.nickname,
                                profileImageUrl = response.author.profileImageUrl,
                                username = response.author.username,
                            ),
                        content = response.content,
                        createdAt = response.createdAt,
                        updatedAt = response.updatedAt,
                    )
                },
            ),
        )
    }

    @GetMapping("/{guestbookId}")
    override fun getGuestbook(
        @PathVariable guestbookId: String,
    ): ResponseEntity<CommonResponse<GuestbookResponse>> {
        log.info { "Getting guestbook: $guestbookId" }

        val query = GetGuestbookUseCase.Query(guestbookId)
        val response = guestbookQueryFacade.getGuestbook().execute(query)

        return ResponseEntity.ok(
            CommonResponse.success(
                GuestbookResponse(
                    guestbookId = response.guestbookId,
                    author =
                        AuthorResponse(
                            memberId = response.author.memberId,
                            nickname = response.author.nickname,
                            profileImageUrl = response.author.profileImageUrl,
                            username = response.author.username,
                        ),
                    content = response.content,
                    createdAt = response.createdAt,
                    updatedAt = response.updatedAt,
                ),
            ),
        )
    }
}
