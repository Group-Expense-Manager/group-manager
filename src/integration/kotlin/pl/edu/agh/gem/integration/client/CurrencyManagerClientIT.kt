package pl.edu.agh.gem.integration.client

import io.kotest.assertions.throwables.shouldThrow
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_ACCEPTABLE
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.stubCurrencyManagerCurrencies
import pl.edu.agh.gem.internal.client.CurrencyManagerClient
import pl.edu.agh.gem.internal.client.CurrencyManagerClientException
import pl.edu.agh.gem.internal.client.RetryableCurrencyManagerClientException
import pl.edu.agh.gem.util.createAvailableCurrenciesResponse
import pl.edu.agh.gem.util.createCurrencyResponse

class CurrencyManagerClientIT(
    private val currencyManagerClient: CurrencyManagerClient,
) : BaseIntegrationSpec({
        should("get available currencies") {
            // given
            val listOfCurrencies = listOf("PLN", "USD", "EUR")
            val currenciesResponse = createAvailableCurrenciesResponse(listOfCurrencies.map { createCurrencyResponse(it) })
            stubCurrencyManagerCurrencies(currenciesResponse)

            // when
            val result = currencyManagerClient.getCurrencies()

            // then
            result.all {
                it.code in listOfCurrencies
            }
        }

        should("throw CurrencyManagerClientException when we send bad request") {
            // given
            val currenciesResponse = createAvailableCurrenciesResponse()
            stubCurrencyManagerCurrencies(currenciesResponse, NOT_ACCEPTABLE)

            // when & then
            shouldThrow<CurrencyManagerClientException> {
                currencyManagerClient.getCurrencies()
            }
        }

        should("throw RetryableCurrencyManagerClientException when client has internal error") {
            // given
            val currenciesResponse = createAvailableCurrenciesResponse()
            stubCurrencyManagerCurrencies(currenciesResponse, INTERNAL_SERVER_ERROR)

            // when & then
            shouldThrow<RetryableCurrencyManagerClientException> {
                currencyManagerClient.getCurrencies()
            }
        }
    })
