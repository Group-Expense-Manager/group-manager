package pl.edu.agh.gem.external.client

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import pl.edu.agh.gem.config.AttachmentStoreProperties
import pl.edu.agh.gem.external.dto.GroupBalanceResponse
import pl.edu.agh.gem.external.dto.toDomain
import pl.edu.agh.gem.headers.HeadersUtils.withAppAcceptType
import pl.edu.agh.gem.internal.client.FinanceAdapterClient
import pl.edu.agh.gem.internal.client.FinanceAdapterClientException
import pl.edu.agh.gem.internal.client.RetryableFinanceAdapterClientException
import pl.edu.agh.gem.internal.model.GroupBalance
import pl.edu.agh.gem.paths.Paths.INTERNAL

@Component
class RestFinanceAdapterClient(
    @Qualifier("FinanceAdapterRestTemplate") val restTemplate: RestTemplate,
    val attachmentStoreProperties: AttachmentStoreProperties,
) : FinanceAdapterClient {

    private fun resolveGetGroupBalance(groupId: String) =
        "${attachmentStoreProperties.url}$INTERNAL/balances/groups/$groupId"

    override fun getGroupBalance(groupId: String): GroupBalance {
        return try {
            restTemplate.exchange(
                resolveGetGroupBalance(groupId),
                GET,
                HttpEntity<Any>(HttpHeaders().withAppAcceptType()),
                GroupBalanceResponse::class.java,
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
