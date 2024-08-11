package pl.edu.agh.gem.external.dto

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.util.createGroup

class GroupMembersResponseTest : ShouldSpec({

    should("correctly map Group to GroupMembersResponse") {
        // given
        val group = createGroup(
            members = setOf(
                Member(userId = "user1"),
                Member(userId = "user2"),
                Member(userId = "user3"),
            ),
        )

        // when
        val groupMembersResponse = group.createGroupMembersResponse()

        // then
        groupMembersResponse.also {
            it.members.map { memberResponse -> memberResponse.id } shouldContainExactly setOf("user1", "user2", "user3")
        }
    }

    should("return an empty list when Group has no members") {
        // given
        val group = createGroup(members = setOf())

        // when
        val groupMembersResponse = group.createGroupMembersResponse()

        // then
        groupMembersResponse.members.shouldBe(listOf())
    }
},)
