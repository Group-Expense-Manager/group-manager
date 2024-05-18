package pl.edu.agh.gem.external.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.gem.external.dto.GroupCreationRequest
import pl.edu.agh.gem.external.dto.GroupCreationResponse
import pl.edu.agh.gem.external.dto.toDomain
import pl.edu.agh.gem.external.dto.toGroupCreationResponse
import pl.edu.agh.gem.internal.generator.CodeGenerator
import pl.edu.agh.gem.internal.service.GroupService
import pl.edu.agh.gem.media.InternalApiMediaType.APPLICATION_JSON_INTERNAL_VER_1
import pl.edu.agh.gem.paths.Paths.EXTERNAL
import pl.edu.agh.gem.security.GemUserId

@RestController
@RequestMapping("$EXTERNAL/groups")
class GroupController(
    private val groupService: GroupService,
    private val codeGenerator: CodeGenerator,
) {

    @PostMapping(consumes = [APPLICATION_JSON_INTERNAL_VER_1], produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(CREATED)
    fun createGroup(
        @GemUserId userId: String,
        @Valid @RequestBody
        groupCreationRequest: GroupCreationRequest,
    ): GroupCreationResponse {
        return groupService.createGroup(
            groupCreationRequest.toDomain(userId, codeGenerator.generateJoinCode()),
        ).toGroupCreationResponse()
    }
}
