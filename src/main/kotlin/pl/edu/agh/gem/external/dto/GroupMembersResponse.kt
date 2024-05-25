package pl.edu.agh.gem.external.dto

import pl.edu.agh.gem.internal.model.Group

data class GroupMembersResponse(
    val members: List<GroupMemberResponse>,
)

data class GroupMemberResponse(
    val id: String,
)

fun Group.createGroupMembersResponse() = GroupMembersResponse(
    members = members.map { GroupMemberResponse(it.userId) },
)
