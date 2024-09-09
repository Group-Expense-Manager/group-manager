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
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
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
import pl.edu.agh.gem.integration.ability.stubGroupBalance
import pl.edu.agh.gem.integration.ability.stubInitGroupAttachment
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.persistence.ArchiveGroupRepository
import pl.edu.agh.gem.internal.persistence.GroupRepository
import pl.edu.agh.gem.internal.service.MissingGroupException
import pl.edu.agh.gem.internal.service.UserAlreadyInGroupException
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_NOT_EMPTY
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_NOT_SUPPORTED
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_PATTERN
import pl.edu.agh.gem.internal.validation.ValidationMessage.NAME_MAX_LENGTH
import pl.edu.agh.gem.internal.validation.ValidationMessage.NAME_NOT_BLANK
import pl.edu.agh.gem.util.createAvailableCurrenciesResponse
import pl.edu.agh.gem.util.createBalanceDto
import pl.edu.agh.gem.util.createGroup
import pl.edu.agh.gem.util.createGroupAttachmentResponse
import pl.edu.agh.gem.util.createGroupBalanceResponse
import pl.edu.agh.gem.util.createGroupCreationRequest
import pl.edu.agh.gem.util.createGroupUpdateCurrencyDto
import pl.edu.agh.gem.util.createGroupUpdateRequest
import pl.edu.agh.gem.util.createUserBalanceDto
import java.math.BigDecimal.TEN

class ExternalGroupControllerIT(
    private val service: ServiceTestClient,
    private val groupRepository: GroupRepository,
    private val archivedGroupRepository: ArchiveGroupRepository,
) : BaseIntegrationSpec({
    should("create group") {
        // given
        val user = createGemUser()
        val currenciesResponse = createAvailableCurrenciesResponse()
        val attachment = createGroupAttachmentResponse()
        stubInitGroupAttachment(attachment, user.id)
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
            Pair(GROUP_CURRENCY_PATTERN, createGroupCreationRequest(groupCurrencies = "")),
            Pair(GROUP_CURRENCY_PATTERN, createGroupCreationRequest(groupCurrencies = "someCurrency")),
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
        val group = createGroup(joinCode = "validJoinCode", members = setOf(Member(user.id)))
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
                members = setOf(Member(userId = user.id), Member(userId = ownersId[index])),
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

    should("return empty list when user does not have any groups") {
        // given
        val user = createGemUser()

        // when
        val response = service.getUserGroups(user)

        // then
        response shouldHaveHttpStatus OK
        response.shouldBody<ExternalUserGroupsResponse> {
            groups shouldHaveSize 0
        }
    }

    should("return group when user has access") {
        // given
        val user = createGemUser()
        val group = createGroup(members = setOf(Member(userId = user.id)))
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
            groupCurrencies.map { it.code } shouldContainExactly group.currencies.map { it.code }
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
        val group = createGroup(members = setOf())
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

    should("delete group") {
        // given
        val user = createGemUser()
        val group = createGroup(members = setOf(Member(userId = user.id)), ownerId = user.id)
        groupRepository.save(group)
        val groupBalanceResponse = createGroupBalanceResponse(groupId = group.id)
        stubGroupBalance(groupBalanceResponse, groupBalanceResponse.id)

        // when
        val response = service.removeGroup(user, group.id)

        // then
        response shouldHaveHttpStatus OK
        val existingGroup = groupRepository.findById(group.id)
        existingGroup.shouldBeNull()
        val archivedGroup = archivedGroupRepository.findById(group.id)
        archivedGroup.shouldNotBeNull()
        archivedGroup.id shouldBe group.id
    }

    should("block delete group when user is not the owner") {
        // given
        val user = createGemUser()
        val authorId = "owner"
        val group = createGroup(members = setOf(Member(userId = authorId), Member(userId = user.id)), ownerId = authorId)
        groupRepository.save(group)
        val groupBalanceResponse = createGroupBalanceResponse(groupId = group.id)
        stubGroupBalance(groupBalanceResponse, groupBalanceResponse.id)

        // when
        val response = service.removeGroup(user, group.id)

        // then
        response shouldHaveHttpStatus UNPROCESSABLE_ENTITY
        val existingGroup = groupRepository.findById(group.id)
        existingGroup.shouldNotBeNull()
        val archivedGroup = archivedGroupRepository.findById(group.id)
        archivedGroup.shouldBeNull()
    }

    should("block delete group when balance is not zero") {
        // given
        val user = createGemUser()
        val group = createGroup(members = setOf(Member(userId = user.id)), ownerId = user.id)
        groupRepository.save(group)
        val groupBalanceResponse = createGroupBalanceResponse(
            groupId = group.id,
            usersBalance = listOf(
                createUserBalanceDto(
                    userId = user.id,
                    balance = listOf(
                        createBalanceDto(
                            currency = "PLN",
                            amount = TEN,
                        ),
                    ),
                ),
            ),
        )
        stubGroupBalance(groupBalanceResponse, groupBalanceResponse.id)

        // when
        val response = service.removeGroup(user, group.id)

        // then
        response shouldHaveHttpStatus UNPROCESSABLE_ENTITY
        val existingGroup = groupRepository.findById(group.id)
        existingGroup.shouldNotBeNull()
        val archivedGroup = archivedGroupRepository.findById(group.id)
        archivedGroup.shouldBeNull()
    }

    should("block delete group when group does not exist") {
        // given
        val user = createGemUser()
        val group = createGroup(members = setOf(Member(userId = user.id)), ownerId = user.id)

        // when
        val response = service.removeGroup(user, group.id)

        // then
        response shouldHaveHttpStatus NOT_FOUND
    }

    should("update group successfully") {
        // given
        val user = createGemUser()
        val existingGroup = createGroup(
            ownerId = user.id,
            members = setOf(Member(userId = user.id)),
        )
        groupRepository.save(existingGroup)
        val currenciesResponse = createAvailableCurrenciesResponse()
        stubCurrencyManagerCurrencies(currenciesResponse)
        val groupUpdateRequest = createGroupUpdateRequest(
            name = "Updated Group Name",
            groupCurrencies = listOf(createGroupUpdateCurrencyDto(code = "PLN")),
        )

        // when
        val response = service.updateGroup(groupUpdateRequest, user, existingGroup.id)

        // then
        response shouldHaveHttpStatus OK
        response.shouldBody<ExternalGroupResponse> {
            groupId shouldBe existingGroup.id
            name shouldBe groupUpdateRequest.name
            groupCurrencies.map { it.code } shouldContainExactly groupUpdateRequest.groupCurrencies.map { it.code }
        }
    }

    context("return validation exception cause:") {
        withData(
            nameFn = { it.first },
            Pair(NAME_NOT_BLANK, createGroupUpdateRequest(name = "")),
            Pair(NAME_MAX_LENGTH, createGroupUpdateRequest(name = "name".repeat(10))),
            Pair(GROUP_CURRENCY_NOT_EMPTY, createGroupUpdateRequest(groupCurrencies = listOf())),
            Pair(GROUP_CURRENCY_PATTERN, createGroupUpdateRequest(groupCurrencies = listOf(createGroupUpdateCurrencyDto(code = "AA")))),
            Pair(GROUP_CURRENCY_PATTERN, createGroupUpdateRequest(groupCurrencies = listOf(createGroupUpdateCurrencyDto(code = "")))),
        ) { (expectedMessage, groupUpdateRequest) ->
            // given
            val user = createGemUser()
            val existingGroup = createGroup(
                ownerId = user.id,
                members = setOf(Member(userId = user.id)),
            )
            groupRepository.save(existingGroup)
            val currenciesResponse = createAvailableCurrenciesResponse()
            stubCurrencyManagerCurrencies(currenciesResponse)

            // when
            val response = service.updateGroup(groupUpdateRequest, user, existingGroup.id)

            // then
            response shouldHaveHttpStatus BAD_REQUEST
            response shouldHaveValidationError expectedMessage
        }
    }

    should("return BAD_REQUEST when user is not the owner") {
        // given
        val owner = createGemUser(
            id = "ownerId",
        )
        val otherUser = createGemUser(
            id = "otherUserId",
        )
        val existingGroup = createGroup(
            ownerId = owner.id,
            members = setOf(Member(userId = owner.id)),
        )
        groupRepository.save(existingGroup)
        val currenciesResponse = createAvailableCurrenciesResponse()
        stubCurrencyManagerCurrencies(currenciesResponse)

        val groupUpdateRequest = createGroupUpdateRequest(
            name = "Unauthorized Update",
            groupCurrencies = listOf(createGroupUpdateCurrencyDto(code = "PLN")),
        )

        // when
        val response = service.updateGroup(groupUpdateRequest, otherUser, existingGroup.id)

        // then
        response shouldHaveHttpStatus BAD_REQUEST
    }

    should("return NOT_FOUND when group does not exist") {
        // given
        val user = createGemUser()
        val nonExistentGroupId = "nonExistentGroup"
        val currenciesResponse = createAvailableCurrenciesResponse()
        stubCurrencyManagerCurrencies(currenciesResponse)
        val groupUpdateRequest = createGroupUpdateRequest(
            name = "Non-Existent Group",
            groupCurrencies = listOf(createGroupUpdateCurrencyDto(code = "PLN")),
        )

        // when
        val response = service.updateGroup(groupUpdateRequest, user, nonExistentGroupId)

        // then
        response shouldHaveHttpStatus NOT_FOUND
    }
},)
