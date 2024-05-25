package pl.edu.agh.gem.external.dto

import pl.edu.agh.gem.internal.model.Group

data class GroupCreationResponse(
    val groupId: String,
    val joinCode: String,
)

fun Group.toGroupCreationResponse() = GroupCreationResponse(
    groupId = id,
    joinCode = joinCode,
)
