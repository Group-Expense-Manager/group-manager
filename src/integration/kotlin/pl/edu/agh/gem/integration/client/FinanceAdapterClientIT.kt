package pl.edu.agh.gem.integration.client

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_ACCEPTABLE
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.stubGroupBalance
import pl.edu.agh.gem.internal.client.FinanceAdapterClient
import pl.edu.agh.gem.internal.client.FinanceAdapterClientException
import pl.edu.agh.gem.internal.client.RetryableFinanceAdapterClientException
import pl.edu.agh.gem.util.createBalancesResponse

class FinanceAdapterClientIT(
    private val financeAdapterClient: FinanceAdapterClient,
) : BaseIntegrationSpec({
        should("get group balance") {
            // given
            val response = createBalancesResponse()
            stubGroupBalance(response, response.groupId)

            // when
            val result = financeAdapterClient.getGroupBalance(response.groupId)

            // then
            result shouldBe response.toDomain()
        }

        should("throw FinanceAdapterClientException when we send bad request") {
            // given
            val response = createBalancesResponse()
            stubGroupBalance(response, response.groupId, NOT_ACCEPTABLE)

            // when & then
            shouldThrow<FinanceAdapterClientException> {
                financeAdapterClient.getGroupBalance(response.groupId)
            }
        }

        should("throw RetryableFinanceAdapterClientException when client has internal error") {
            // given
            val response = createBalancesResponse()
            stubGroupBalance(response, response.groupId, INTERNAL_SERVER_ERROR)

            // when & then
            shouldThrow<RetryableFinanceAdapterClientException> {
                financeAdapterClient.getGroupBalance(response.groupId)
            }
        }
    })
