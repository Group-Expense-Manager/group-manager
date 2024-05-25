package pl.edu.agh.gem.external.controller

import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.edu.agh.gem.external.dto.InternalGroupResponse
import pl.edu.agh.gem.external.dto.toInternalGroupResponse
import pl.edu.agh.gem.internal.service.GroupService
import pl.edu.agh.gem.media.InternalApiMediaType.APPLICATION_JSON_INTERNAL_VER_1
import pl.edu.agh.gem.paths.Paths.INTERNAL

@RestController
@RequestMapping("$INTERNAL/groups")
class InternalGroupController(
    private val groupService: GroupService,
) {

    @GetMapping("/{groupId}", produces = [APPLICATION_JSON_INTERNAL_VER_1])
    @ResponseStatus(OK)
    fun getGroup(
        @PathVariable groupId: String,
    ): InternalGroupResponse {
        return groupService.getGroup(groupId).toInternalGroupResponse()
    }
}
