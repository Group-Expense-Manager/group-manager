package pl.edu.agh.gem.external.dto

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.util.createGroup

class ExternalGroupResponseTest : ShouldSpec({

    should("correctly map Group to ExternalGroupResponse") {
        // given
        val group = createGroup(
            members = setOf(
                Member(userId = "user1"),
                Member(userId = "user2"),
                Member(userId = "user3"),
            ),
            currencies = setOf(
                Currency(code = "USD"),
                Currency(code = "EUR"),
            ),
            id = "group1",
            name = "Test Group",
            ownerId = "owner1",
            joinCode = "join123",
            attachmentId = "attachment456",
        )

        // when
        val externalGroupResponse = group.toExternalGroupResponse()

        // then
        externalGroupResponse.groupId.shouldBe("group1")
        externalGroupResponse.name.shouldBe("Test Group")
        externalGroupResponse.ownerId.shouldBe("owner1")
        externalGroupResponse.members.map { it.userId } shouldContainExactly listOf("user1", "user2", "user3")
        externalGroupResponse.groupCurrencies.map { it.code } shouldContainExactly listOf("USD", "EUR")
        externalGroupResponse.joinCode.shouldBe("join123")
        externalGroupResponse.attachmentId.shouldBe("attachment456")
    }

    should("return an empty list when Group has no members") {
        // given
        val group = createGroup(
            members = setOf(),
            currencies = setOf(
                Currency(code = "USD"),
                Currency(code = "EUR"),
            ),
            id = "group1",
            name = "Test Group",
            ownerId = "owner1",
            joinCode = "join123",
            attachmentId = "attachment456",
        )

        // when
        val externalGroupResponse = group.toExternalGroupResponse()

        // then
        externalGroupResponse.groupId.shouldBe("group1")
        externalGroupResponse.name.shouldBe("Test Group")
        externalGroupResponse.ownerId.shouldBe("owner1")
        externalGroupResponse.members.shouldBe(listOf())
        externalGroupResponse.groupCurrencies.map { it.code } shouldContainExactly listOf("USD", "EUR")
        externalGroupResponse.joinCode.shouldBe("join123")
        externalGroupResponse.attachmentId.shouldBe("attachment456")
    }
},)
