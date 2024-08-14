package pl.edu.agh.gem.integration.client

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_ACCEPTABLE
import pl.edu.agh.gem.external.dto.toDomain
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.stubGroupBalance
import pl.edu.agh.gem.internal.client.FinanceAdapterClient
import pl.edu.agh.gem.internal.client.FinanceAdapterClientException
import pl.edu.agh.gem.internal.client.RetryableFinanceAdapterClientException
import pl.edu.agh.gem.util.createGroupBalanceResponse

class FinanceAdapterClientIT(
    private val financeAdapterClient: FinanceAdapterClient,
) : BaseIntegrationSpec({
    should("get group balance") {
        // given
        val response = createGroupBalanceResponse()
        stubGroupBalance(response, response.id)

        // when
        val result = financeAdapterClient.getGroupBalance(response.id)

        // then
        result shouldBe response.toDomain()
    }

    should("throw FinanceAdapterClientException when we send bad request") {
        // given
        val response = createGroupBalanceResponse()
        stubGroupBalance(response, response.id, NOT_ACCEPTABLE)

        // when & then
        shouldThrow<FinanceAdapterClientException> {
            financeAdapterClient.getGroupBalance(response.id)
        }
    }

    should("throw RetryableFinanceAdapterClientException when client has internal error") {
        // given
        val response = createGroupBalanceResponse()
        stubGroupBalance(response, response.id, INTERNAL_SERVER_ERROR)

        // when & then
        shouldThrow<RetryableFinanceAdapterClientException> {
            financeAdapterClient.getGroupBalance(response.id)
        }
    }
},)
