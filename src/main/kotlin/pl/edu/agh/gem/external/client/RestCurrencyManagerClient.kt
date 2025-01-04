package pl.edu.agh.gem.external.client

import io.github.resilience4j.retry.annotation.Retry
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import pl.edu.agh.gem.config.CurrencyManagerProperties
import pl.edu.agh.gem.external.dto.AvailableCurrenciesResponse
import pl.edu.agh.gem.external.dto.toDomain
import pl.edu.agh.gem.headers.HeadersUtils.withAppAcceptType
import pl.edu.agh.gem.internal.client.CurrencyManagerClient
import pl.edu.agh.gem.internal.client.CurrencyManagerClientException
import pl.edu.agh.gem.internal.client.RetryableCurrencyManagerClientException
import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.metrics.MeteredClient
import pl.edu.agh.gem.paths.Paths.INTERNAL

@Component
@MeteredClient
class RestCurrencyManagerClient(
    @Qualifier("CurrencyManagerRestTemplate") val restTemplate: RestTemplate,
    val currencyManagerProperties: CurrencyManagerProperties,
) : CurrencyManagerClient {

    @Retry(name = "currencyManager")
    override fun getCurrencies(): List<Currency> {
        return try {
            restTemplate.exchange(
                resolveAvailableCurrenciesAddress(),
                GET,
                HttpEntity<Any>(HttpHeaders().withAppAcceptType()),
                AvailableCurrenciesResponse::class.java,
            ).body?.toDomain() ?: throw CurrencyManagerClientException("While trying to retrieve available currencies we receive empty body")
        } catch (ex: HttpClientErrorException) {
            logger.warn(ex) { "Client side exception while trying to retrieve available currencies" }
            throw CurrencyManagerClientException(ex.message)
        } catch (ex: HttpServerErrorException) {
            logger.warn(ex) { "Server side exception while trying to retrieve available currencies" }
            throw RetryableCurrencyManagerClientException(ex.message)
        } catch (ex: Exception) {
            logger.warn(ex) { "Unexpected exception while trying to retrieve available currencies" }
            throw CurrencyManagerClientException(ex.message)
        }
    }

    private fun resolveAvailableCurrenciesAddress() =
        "${currencyManagerProperties.url}$INTERNAL/currencies"

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
