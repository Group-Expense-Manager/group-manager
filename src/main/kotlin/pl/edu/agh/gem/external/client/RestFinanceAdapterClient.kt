package pl.edu.agh.gem.external.client

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import pl.edu.agh.gem.config.FinanceAdapterProperties
import pl.edu.agh.gem.external.dto.BalancesResponse
import pl.edu.agh.gem.headers.HeadersUtils.withAppAcceptType
import pl.edu.agh.gem.internal.client.FinanceAdapterClient
import pl.edu.agh.gem.internal.client.FinanceAdapterClientException
import pl.edu.agh.gem.internal.client.RetryableFinanceAdapterClientException
import pl.edu.agh.gem.internal.model.Balances
import pl.edu.agh.gem.metrics.MeteredClient
import pl.edu.agh.gem.paths.Paths.INTERNAL

@Component
@MeteredClient
class RestFinanceAdapterClient(
    @Qualifier("FinanceAdapterRestTemplate") val restTemplate: RestTemplate,
    val financeAdapterProperties: FinanceAdapterProperties,
) : FinanceAdapterClient {
    private fun resolveGetGroupBalance(groupId: String) = "${financeAdapterProperties.url}$INTERNAL/balances/groups/$groupId"

    override fun getGroupBalance(groupId: String): List<Balances> {
        return try {
            restTemplate.exchange(
                resolveGetGroupBalance(groupId),
                GET,
                HttpEntity<Any>(HttpHeaders().withAppAcceptType()),
                BalancesResponse::class.java,
            ).body?.toDomain() ?: throw FinanceAdapterClientException("While trying to retrieve group balance we receive empty body")
        } catch (ex: HttpClientErrorException) {
            logger.warn(ex) { "Client side exception while trying to retrieve group balance" }
            throw FinanceAdapterClientException(ex.message)
        } catch (ex: HttpServerErrorException) {
            logger.warn(ex) { "Server side exception while trying to retrieve group balance" }
            throw RetryableFinanceAdapterClientException(ex.message)
        } catch (ex: Exception) {
            logger.warn(ex) { "Unexpected exception while trying to retrieve group balance" }
            throw FinanceAdapterClientException(ex.message)
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
