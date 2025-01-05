package pl.edu.agh.gem.external.dto

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.internal.model.Group
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.util.createGroup

class ExternalUserGroupsResponseTest : ShouldSpec({

    should("correctly map Group to ExternalUserGroupsResponse when user in one group") {
        // given
        val userId = "userId"
        val groups =
            listOf(
                createGroup(
                    ownerId = userId,
                    members = setOf(Member(userId = userId)),
                ),
            )

        // when
        val externalUserGroupsResponse = groups.toExternalUserGroupsResponse()

        // then
        externalUserGroupsResponse.groups.size shouldBe 1
        externalUserGroupsResponse.groups.first().also {
            it.groupId shouldBe groups.first().id
            it.name shouldBe groups.first().name
            it.attachmentId shouldBe groups.first().attachmentId
        }
    }

    should("correctly map Group to ExternalUserGroupsResponse when user in multiple groups") {
        // given
        val userId = "userId"
        val groupsId = listOf("group1", "group2", "group3")
        val groupsName = listOf("Group 1", "Group 2", "Group 3")
        val groupsAttachmentId = listOf("attachment1", "attachment2", "attachment3")
        val groups =
            groupsId.mapIndexed { index, groupId ->
                createGroup(
                    id = groupId,
                    ownerId = userId,
                    name = groupsName[index],
                    attachmentId = groupsAttachmentId[index],
                    members = setOf(Member(userId = userId)),
                )
            }
        // when
        val externalUserGroupsResponse = groups.toExternalUserGroupsResponse()

        // then
        externalUserGroupsResponse.groups.also {
            it.size shouldBe 3
            it.map { groupDto -> groupDto.groupId } shouldContainExactly groupsId
            it.map { groupDto -> groupDto.name } shouldContainExactly groupsName
            it.map { groupDto -> groupDto.attachmentId } shouldContainExactly groupsAttachmentId
        }
    }

    should("return an empty list when there are no Groups") {
        // given
        val groups = listOf<Group>()

        // when
        val externalUserGroupsResponse = groups.toExternalUserGroupsResponse()

        // then
        externalUserGroupsResponse.groups shouldBe listOf()
    }
})
