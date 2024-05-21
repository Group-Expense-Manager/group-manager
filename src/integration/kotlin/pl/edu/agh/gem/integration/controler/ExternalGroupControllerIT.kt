package pl.edu.agh.gem.integration.controler

import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContainDuplicates
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import pl.edu.agh.gem.assertion.shouldBody
import pl.edu.agh.gem.assertion.shouldHaveErrors
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.assertion.shouldHaveValidationError
import pl.edu.agh.gem.assertion.shouldHaveValidatorError
import pl.edu.agh.gem.external.dto.GroupCreationResponse
import pl.edu.agh.gem.helper.user.createGemUser
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.integration.ability.stubCurrencyManagerCurrencies
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.persistence.GroupRepository
import pl.edu.agh.gem.internal.service.MissingGroupException
import pl.edu.agh.gem.internal.service.UserAlreadyInGroupException
import pl.edu.agh.gem.internal.validation.ValidationMessage.ATTACHMENT_ID_NOT_BLANK
import pl.edu.agh.gem.internal.validation.ValidationMessage.BASE_CURRENCY_NOT_BLANK
import pl.edu.agh.gem.internal.validation.ValidationMessage.BASE_CURRENCY_NOT_SUPPORTED
import pl.edu.agh.gem.internal.validation.ValidationMessage.BASE_CURRENCY_PATTERN
import pl.edu.agh.gem.internal.validation.ValidationMessage.COLOR_MAX_VALUE
import pl.edu.agh.gem.internal.validation.ValidationMessage.COLOR_MIN_VALUE
import pl.edu.agh.gem.internal.validation.ValidationMessage.NAME_MAX_LENGTH
import pl.edu.agh.gem.internal.validation.ValidationMessage.NAME_NOT_BLANK
import pl.edu.agh.gem.util.createAvailableCurrenciesResponse
import pl.edu.agh.gem.util.createGroup
import pl.edu.agh.gem.util.createGroupCreationRequest
import java.lang.Long.MAX_VALUE
import java.lang.Long.MIN_VALUE

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
            Pair(COLOR_MIN_VALUE, createGroupCreationRequest(color = MIN_VALUE)),
            Pair(COLOR_MAX_VALUE, createGroupCreationRequest(color = MAX_VALUE)),
            Pair(BASE_CURRENCY_NOT_BLANK, createGroupCreationRequest(baseCurrency = "")),
            Pair(BASE_CURRENCY_PATTERN, createGroupCreationRequest(baseCurrency = "someCurrency")),
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

    should("return validator exception cause BASE_CURRENCY_NOT_SUPPORTED") {
        // given
        val user = createGemUser()
        val currenciesResponse = createAvailableCurrenciesResponse()
        stubCurrencyManagerCurrencies(currenciesResponse)
        val createGroupRequest = createGroupCreationRequest(baseCurrency = "STH")

        // when
        val response = service.createGroup(createGroupRequest, user)

        // then
        response shouldHaveHttpStatus BAD_REQUEST
        response shouldHaveValidatorError BASE_CURRENCY_NOT_SUPPORTED
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
},)
