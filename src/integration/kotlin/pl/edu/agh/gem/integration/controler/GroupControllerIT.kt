package pl.edu.agh.gem.integration.controler

import io.kotest.datatest.withData
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import pl.edu.agh.gem.assertion.shouldBody
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.assertion.shouldHaveValidationError
import pl.edu.agh.gem.assertion.shouldHaveValidatorError
import pl.edu.agh.gem.external.dto.GroupCreationResponse
import pl.edu.agh.gem.helper.user.createGemUser
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.integration.ability.stubCurrencyManagerCurrencies
import pl.edu.agh.gem.internal.validation.ValidationMessage.ATTACHMENT_ID_NOT_BLANK
import pl.edu.agh.gem.internal.validation.ValidationMessage.COLOR_MAX_VALUE
import pl.edu.agh.gem.internal.validation.ValidationMessage.COLOR_MIN_VALUE
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_NOT_BLANK
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_NOT_SUPPORTED
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_PATTERN
import pl.edu.agh.gem.internal.validation.ValidationMessage.NAME_MAX_LENGTH
import pl.edu.agh.gem.internal.validation.ValidationMessage.NAME_NOT_BLANK
import pl.edu.agh.gem.util.createAvailableCurrenciesResponse
import pl.edu.agh.gem.util.createGroupCreationRequest
import java.lang.Long.MAX_VALUE
import java.lang.Long.MIN_VALUE

class GroupControllerIT(
    private val service: ServiceTestClient,
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
},)
