package pl.edu.agh.gem.external.dto

import pl.edu.agh.gem.internal.model.Group

data class ExternalUserGroupsResponse(
    val groups: List<ExternalUserGroupsDto>,
)

data class ExternalUserGroupsDto(
    val groupId: String,
    val ownerId: String,
    val name: String,
    val attachmentId: String,
)

fun List<Group>.toExternalUserGroupsResponse() =
    ExternalUserGroupsResponse(
        groups = map { ExternalUserGroupsDto(it.id, it.ownerId, it.name, it.attachmentId) },
    )
