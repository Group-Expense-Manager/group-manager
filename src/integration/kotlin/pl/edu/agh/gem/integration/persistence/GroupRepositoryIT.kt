package pl.edu.agh.gem.integration.persistence

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.internal.persistence.GroupRepository
import pl.edu.agh.gem.util.createGroup

class GroupRepositoryIT(
    private val groupRepository: GroupRepository,
) : BaseIntegrationSpec({
        should("save with unique join code group") {
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

        should("find group by join code") {
            // given
            val group = createGroup(joinCode = "uniqueJoinCode")
            groupRepository.insertWithUniqueJoinCode(group)

            // when
            val foundGroup = groupRepository.findByJoinCode("uniqueJoinCode")

            // then
            foundGroup.shouldNotBeNull()
            foundGroup.shouldBe(group)
        }

        should("return null if group with join code does not exist") {
            // when
            val foundGroup = groupRepository.findByJoinCode("nonExistentJoinCode")

            // then
            foundGroup.shouldBeNull()
        }

        should("save group correctly") {
            // given
            val group = createGroup()

            // when
            val savedGroup = groupRepository.save(group)

            // then
            savedGroup.shouldBe(group)

            // and it can be found
            val foundGroup = groupRepository.findByJoinCode(group.joinCode)
            foundGroup.shouldNotBeNull()
            foundGroup.shouldBe(savedGroup)
        }

        should("update group correctly") {
            // given
            val group = createGroup()
            groupRepository.insertWithUniqueJoinCode(group)

            val updatedGroup = group.copy(name = "Updated Group Name")

            // when
            val result = groupRepository.save(updatedGroup)

            // then
            result.shouldBe(updatedGroup)

            // and it can be found
            val foundGroup = groupRepository.findByJoinCode(updatedGroup.joinCode)
            foundGroup.shouldNotBeNull()
            foundGroup.shouldBe(updatedGroup)
        }

        should("find group by id") {
            // given
            val group = createGroup()
            groupRepository.save(group)

            // when
            val foundGroup = groupRepository.findById(group.id)
            // then
            foundGroup.shouldNotBeNull()
            foundGroup.shouldBe(group)
        }

        should("return null if group with id does not exist") {
            // when
            val foundGroup = groupRepository.findById("nonExistentGroupId")

            // then
            foundGroup.shouldBeNull()
        }

        should("remove group correctly") {
            // given
            val group = createGroup()
            groupRepository.save(group)

            // when
            groupRepository.remove(group)

            // then
            groupRepository.findById(group.id).shouldBeNull()
        }
    })
