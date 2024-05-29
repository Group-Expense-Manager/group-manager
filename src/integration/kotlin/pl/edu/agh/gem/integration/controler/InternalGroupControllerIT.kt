package pl.edu.agh.gem.integration.controler

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import pl.edu.agh.gem.assertion.shouldBody
import pl.edu.agh.gem.assertion.shouldHaveErrors
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.external.dto.ExternalUserGroupsResponse
import pl.edu.agh.gem.external.dto.InternalGroupResponse
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.persistence.GroupRepository
import pl.edu.agh.gem.internal.service.MissingGroupException
import pl.edu.agh.gem.internal.service.UserWithoutGroupException
import pl.edu.agh.gem.util.createGroup

class InternalGroupControllerIT(
    private val service: ServiceTestClient,
    private val groupRepository: GroupRepository,
) : BaseIntegrationSpec({
    should("get group successfully") {
        // given
        val members = listOf(
            Member(userId = "user1"),
            Member(userId = "user2"),
        )
        val group = createGroup(members = members)
        groupRepository.save(group)

        // when
        val response = service.getInternalGroup(group.id)

        // then
        response shouldHaveHttpStatus OK
        response.shouldBody<InternalGroupResponse> {
            members.map { it.userId } shouldContainExactly members.map { it.userId }
            groupCurrencies.map { it.code } shouldContainExactly group.groupCurrencies.map { it.code }
            acceptRequired shouldBe group.acceptRequired
        }
    }

    should("return NOT_FOUND when group does not exist") {
        // given
        val groupId = "nonexistentGroupId"

        // when
        val response = service.getInternalGroup(groupId)

        // then
        response shouldHaveHttpStatus NOT_FOUND
        response shouldHaveErrors {
            errors shouldHaveSize 1
            errors.first().code shouldBe MissingGroupException::class.simpleName
        }
    }

    should("return groups for the user") {
        // given
        val userId = "userId"

        val groupsId = listOf("group1", "group2", "group3")
        val ownersId = listOf("owner1", "owner2", "owner3")
        val groupsName = listOf("Group 1", "Group 2", "Group 3")
        val groupsAttachmentId = listOf("attachment1", "attachment2", "attachment3")
        val joinCodes = listOf("joinCode1", "joinCode2", "joinCode3")

        val groupList = groupsId.mapIndexed { index, groupId ->
            createGroup(
                id = groupId,
                ownerId = ownersId[index],
                name = groupsName[index],
                attachmentId = groupsAttachmentId[index],
                joinCode = joinCodes[index],
                members = listOf(Member(userId = userId), Member(userId = ownersId[index])),
            )
        }

        groupList.forEach(groupRepository::save)

        // when
        val response = service.getInternalUserGroups(userId)

        // then
        response shouldHaveHttpStatus OK
        response.shouldBody<ExternalUserGroupsResponse> {
            groups.map { it.groupId } shouldContainExactly groupsId
            groups.map { it.name } shouldContainExactly groupsName
            groups.map { it.attachmentId } shouldContainExactly groupsAttachmentId
        }
    }

    should("return NOT_FOUND when user does not have any groups") {
        // given
        val userId = "userId"

        // when
        val response = service.getInternalUserGroups(userId)

        // then
        response shouldHaveHttpStatus NOT_FOUND
        response shouldHaveErrors {
            errors shouldHaveSize 1
            errors.first().code shouldBe UserWithoutGroupException::class.simpleName
        }
    }
},)
