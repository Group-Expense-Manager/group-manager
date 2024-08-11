package pl.edu.agh.gem.external.dto

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.util.createGroup

class InternalGroupResponseTest : ShouldSpec({

    should("correctly map Group to InternalGroupResponse") {
        // given
        val group = createGroup(
            members = setOf(
                Member(userId = "user1"),
                Member(userId = "user2"),
                Member(userId = "user3"),
            ),
        )

        // when
        val internalGroupResponse = group.toInternalGroupResponse()

        // then
        internalGroupResponse.members.map { it.id } shouldContainExactly setOf("user1", "user2", "user3")
        internalGroupResponse.groupCurrencies.map { it.code } shouldContainExactly group.currencies.map { it.code }
        internalGroupResponse.acceptRequired.shouldBe(group.acceptRequired)
    }

    should("return an empty list when Group has no members") {
        // given
        val group = createGroup(members = setOf())

        // when
        val internalGroupResponse = group.toInternalGroupResponse()

        // then
        internalGroupResponse.members.shouldBe(listOf())
        internalGroupResponse.groupCurrencies.map { it.code } shouldContainExactly group.currencies.map { it.code }
        internalGroupResponse.acceptRequired.shouldBe(group.acceptRequired)
    }
},)
