package pl.edu.agh.gem.integration.controler

import io.kotest.matchers.collections.shouldContainExactly
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import pl.edu.agh.gem.assertion.shouldBody
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.external.dto.GroupMembersResponse
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.persistence.GroupRepository
import pl.edu.agh.gem.util.createGroup

class MembersControllerIT(
    private val service: ServiceTestClient,
    private val groupRepository: GroupRepository,
) : BaseIntegrationSpec({
    should("return group members when group exists") {
        // given
        val members = setOf(
            Member(userId = "user1"),
            Member(userId = "user2"),
        )
        val group = createGroup(members = members)
        groupRepository.save(group)

        // when
        val result = service.getMembers(group.id)

        // then
        result shouldHaveHttpStatus OK
        result.shouldBody<GroupMembersResponse> {
            this.members.map { it.id } shouldContainExactly members.map { it.userId }
        }
    }

    should("return 404 when group does not exist") {
        // given
        val groupId = "nonExistentGroupId"

        // when
        val result = service.getMembers(groupId)

        // then
        result shouldHaveHttpStatus NOT_FOUND
    }
},)
