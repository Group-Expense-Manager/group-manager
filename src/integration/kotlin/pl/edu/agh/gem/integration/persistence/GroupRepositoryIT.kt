package pl.edu.agh.gem.integration.persistence

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.internal.persistence.GroupRepository
import pl.edu.agh.gem.util.createGroup

class GroupRepositoryIT(
    private val groupRepository: GroupRepository,
) : BaseIntegrationSpec({
    should("save group") {
        // given
        val group = createGroup()

        // when
        val result = groupRepository.insertWithUniqueJoinCode(group)

        // then
        result.shouldBe(group)
    }

    should("save group with another join code when duplicate key exception occurs") {
        // given
        val group = createGroup(id = "group", joinCode = "joinCode")
        val otherGroup = createGroup(id = "otherGroupId", joinCode = "joinCode")
        groupRepository.insertWithUniqueJoinCode(otherGroup)

        // when
        val result = groupRepository.insertWithUniqueJoinCode(group)

        // then
        result.joinCode shouldNotBe group.joinCode
    }
},)
