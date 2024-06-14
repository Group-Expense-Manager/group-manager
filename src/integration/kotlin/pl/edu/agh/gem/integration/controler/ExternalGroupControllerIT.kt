package pl.edu.agh.gem.integration.controler

import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContainDuplicates
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import pl.edu.agh.gem.assertion.shouldBody
import pl.edu.agh.gem.assertion.shouldHaveErrors
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.assertion.shouldHaveValidationError
import pl.edu.agh.gem.assertion.shouldHaveValidatorError
import pl.edu.agh.gem.exception.UserWithoutGroupAccessException
import pl.edu.agh.gem.external.dto.ExternalGroupResponse
import pl.edu.agh.gem.external.dto.ExternalUserGroupsResponse
import pl.edu.agh.gem.external.dto.GroupCreationResponse
import pl.edu.agh.gem.helper.user.createGemUser
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.integration.ability.stubCurrencyManagerCurrencies
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.persistence.GroupRepository
import pl.edu.agh.gem.internal.service.MissingGroupException
import pl.edu.agh.gem.internal.service.UserAlreadyInGroupException
import pl.edu.agh.gem.internal.service.UserWithoutGroupException
import pl.edu.agh.gem.internal.validation.ValidationMessage.ATTACHMENT_ID_NOT_BLANK
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_NOT_BLANK
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_NOT_SUPPORTED
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_PATTERN
import pl.edu.agh.gem.internal.validation.ValidationMessage.NAME_MAX_LENGTH
import pl.edu.agh.gem.internal.validation.ValidationMessage.NAME_NOT_BLANK
import pl.edu.agh.gem.util.createAvailableCurrenciesResponse
import pl.edu.agh.gem.util.createGroup
import pl.edu.agh.gem.util.createGroupCreationRequest

class ExternalGroupControllerIT(
    private val service: ServiceTestClient,
    private val groupRepository: GroupRepository,
) : BaseIntegrationSpec({
    should("create group") {
        // given
        val user = createGemUser()
        val currenciesResponse = createAvailableCurrenciesResponse()
        stubCurrencyManagerCurrencies(currenciesResponse)
        val createGroupRequest = createGroupCreationRequest()

        // when
        val response = service.createGroup(createGroupRequest, user)

        // then
        response shouldHaveHttpStatus CREATED
        response.shouldBody<GroupCreationResponse> {
            groupId.shouldNotBeNull()
        }
    }

    context("return validation exception cause:") {
        withData(
            nameFn = { it.first },
            Pair(NAME_NOT_BLANK, createGroupCreationRequest(name = "")),
            Pair(NAME_MAX_LENGTH, createGroupCreationRequest(name = "name".repeat(10))),
            Pair(GROUP_CURRENCY_NOT_BLANK, createGroupCreationRequest(groupCurrencies = "")),
            Pair(GROUP_CURRENCY_PATTERN, createGroupCreationRequest(groupCurrencies = "someCurrency")),
            Pair(ATTACHMENT_ID_NOT_BLANK, createGroupCreationRequest(attachmentId = "")),
        ) { (expectedMessage, createGroupRequest) ->
            // given
            val user = createGemUser()
            val currenciesResponse = createAvailableCurrenciesResponse()
            stubCurrencyManagerCurrencies(currenciesResponse)

            // when
            val response = service.createGroup(createGroupRequest, user)

            // then
            response shouldHaveHttpStatus BAD_REQUEST
            response shouldHaveValidationError expectedMessage
        }
    }

    should("return validator exception cause GROUP_CURRENCY_NOT_SUPPORTED") {
        // given
        val user = createGemUser()
        val currenciesResponse = createAvailableCurrenciesResponse()
        stubCurrencyManagerCurrencies(currenciesResponse)
        val createGroupRequest = createGroupCreationRequest(groupCurrencies = "STH")

        // when
        val response = service.createGroup(createGroupRequest, user)

        // then
        response shouldHaveHttpStatus BAD_REQUEST
        response shouldHaveValidatorError GROUP_CURRENCY_NOT_SUPPORTED
    }

    should("join group successfully") {
        // given
        val user = createGemUser()
        val group = createGroup(joinCode = "validJoinCode")
        groupRepository.save(group)

        // when
        val response = service.joinGroup(group.joinCode, user)

        // then
        response shouldHaveHttpStatus OK
        groupRepository.findByJoinCode(group.joinCode).apply {
            shouldNotBeNull()
            members.map { member -> member.userId }.shouldContain(user.id)
        }
    }

    should("return NOT_FOUND when join code does not exist") {
        // given
        val user = createGemUser()
        val joinCode = "invalidJoinCode"

        // when
        val response = service.joinGroup(joinCode, user)

        // then
        response shouldHaveHttpStatus NOT_FOUND
        response shouldHaveErrors {
            errors shouldHaveSize 1
            errors.first().code shouldBe MissingGroupException::class.simpleName
        }
        groupRepository.findByJoinCode(joinCode).shouldBeNull()
    }

    should("return UserAlreadyInGroupException when user is already in the group") {
        // given
        val user = createGemUser()
        val group = createGroup(joinCode = "validJoinCode", members = listOf(Member(user.id)))
        groupRepository.save(group)

        // when
        val response = service.joinGroup(group.joinCode, user)

        // then
        response shouldHaveHttpStatus CONFLICT
        response shouldHaveErrors {
            errors shouldHaveSize 1
            errors.first().code shouldBe UserAlreadyInGroupException::class.simpleName
        }
        groupRepository.findByJoinCode(group.joinCode).apply {
            shouldNotBeNull()
            members.map { member -> member.userId }.shouldNotContainDuplicates()
            members.map { member -> member.userId }.shouldContain(user.id)
        }
    }

    should("return groups for the user") {
        // given
        val user = createGemUser()

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
                members = listOf(Member(userId = user.id), Member(userId = ownersId[index])),
            )
        }

        groupList.forEach(groupRepository::save)

        // when
        val response = service.getUserGroups(user)

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
        val user = createGemUser()

        // when
        val response = service.getUserGroups(user)

        // then
        response shouldHaveHttpStatus NOT_FOUND
        response shouldHaveErrors {
            errors shouldHaveSize 1
            errors.first().code shouldBe UserWithoutGroupException::class.simpleName
        }
    }

    should("return group when user has access") {
        // given
        val user = createGemUser()
        val group = createGroup(members = listOf(Member(userId = user.id)))
        groupRepository.save(group)

        // when
        val response = service.getGroup(user, group.id)

        // then
        response shouldHaveHttpStatus OK
        response.shouldBody<ExternalGroupResponse> {
            groupId shouldBe group.id
            name shouldBe group.name
            ownerId shouldBe group.ownerId
            members.map { it.userId } shouldContainExactly group.members.map { it.userId }
            acceptRequired shouldBe group.acceptRequired
            groupCurrencies.map { it.code } shouldContainExactly group.groupCurrencies.map { it.code }
            joinCode shouldBe group.joinCode
            attachmentId shouldBe group.attachmentId
        }
    }

    should("return NOT_FOUND when group does not exist") {
        // given
        val unknownGroupId = "unknownGroupId"
        val user = createGemUser()

        // when
        val response = service.getGroup(user, unknownGroupId)

        // then
        response shouldHaveHttpStatus NOT_FOUND
        response shouldHaveErrors {
            errors shouldHaveSize 1
            errors.first().code shouldBe MissingGroupException::class.simpleName
        }
    }

    should("return UserWithoutGroupAccessException when user does not have access to the group") {
        // given
        val user = createGemUser()
        val group = createGroup(members = listOf())
        groupRepository.save(group)

        // when
        val response = service.getGroup(user, group.id)

        // then
        response shouldHaveHttpStatus FORBIDDEN
        response shouldHaveErrors {
            errors shouldHaveSize 1
            errors.first().code shouldBe UserWithoutGroupAccessException::class.simpleName
        }
    }
},)
