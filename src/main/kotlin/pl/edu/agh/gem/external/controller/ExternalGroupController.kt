package pl.edu.agh.gem.external.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.gem.exception.UserWithoutGroupAccessException
import pl.edu.agh.gem.external.dto.ExternalGroupResponse
import pl.edu.agh.gem.external.dto.ExternalUserGroupsResponse
import pl.edu.agh.gem.external.dto.GroupCreationRequest
import pl.edu.agh.gem.external.dto.GroupCreationResponse
import pl.edu.agh.gem.external.dto.toExternalGroupResponse
import pl.edu.agh.gem.external.dto.toExternalUserGroupsResponse
import pl.edu.agh.gem.external.dto.toGroupCreationResponse
import pl.edu.agh.gem.external.dto.toNewGroup
import pl.edu.agh.gem.internal.service.GroupService
import pl.edu.agh.gem.media.InternalApiMediaType.APPLICATION_JSON_INTERNAL_VER_1
import pl.edu.agh.gem.paths.Paths.EXTERNAL
import pl.edu.agh.gem.security.GemUserId

@RestController
@RequestMapping("$EXTERNAL/groups")
class ExternalGroupController(
    private val groupService: GroupService,
) {

    @PostMapping(consumes = [APPLICATION_JSON_INTERNAL_VER_1], produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(CREATED)
    fun createGroup(
        @GemUserId userId: String,
        @Valid @RequestBody
        groupCreationRequest: GroupCreationRequest,
    ): GroupCreationResponse {
        return groupService.createGroup(
            groupCreationRequest.toNewGroup(userId),
        ).toGroupCreationResponse()
    }

    @PostMapping("/join/{joinCode}", consumes = [APPLICATION_JSON_INTERNAL_VER_1], produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(OK)
    fun joinGroup(
        @GemUserId userId: String,
        @PathVariable joinCode: String,
    ) {
        groupService.joinGroup(joinCode, userId)
    }

    @GetMapping(produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(OK)
    fun getGroups(
        @GemUserId userId: String,
    ): ExternalUserGroupsResponse {
        return groupService.getUserGroups(userId)
            .toExternalUserGroupsResponse()
    }

    @GetMapping("/{groupId}", produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(OK)
    fun getGroup(
        @GemUserId userId: String,
        @PathVariable groupId: String,
    ): ExternalGroupResponse {
        return groupService.getGroup(groupId).takeIf { it.members.any { member -> member.userId == userId } }
            ?.toExternalGroupResponse()
            ?: throw UserWithoutGroupAccessException(userId)
    }
}
