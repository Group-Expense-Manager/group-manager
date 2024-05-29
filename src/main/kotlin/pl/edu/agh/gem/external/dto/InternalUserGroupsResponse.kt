package pl.edu.agh.gem.external.dto

import pl.edu.agh.gem.internal.model.Group

data class InternalUserGroupsResponse(
    val groups: List<InternalUserGroupsDto>,
)

data class InternalUserGroupsDto(
    val groupId: String,
    val name: String,
    val attachmentId: String,
)

fun List<Group>.toInternalUserGroupsResponse() = InternalUserGroupsResponse(
    groups = map { InternalUserGroupsDto(it.id, it.name, it.attachmentId) },
)
