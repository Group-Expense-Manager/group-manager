package pl.edu.agh.gem.external.dto

import pl.edu.agh.gem.internal.model.Group

data class ExternalUserGroupsResponse(
    val groups: List<UserGroupsDto>,
)

data class UserGroupsDto(
    val groupId: String,
    val name: String,
    val color: Long,
    val attachmentId: String,
)

fun List<Group>.toExternalUserGroupsResponse() = ExternalUserGroupsResponse(
    groups = map { UserGroupsDto(it.id, it.name, it.color, it.attachmentId) },
)
