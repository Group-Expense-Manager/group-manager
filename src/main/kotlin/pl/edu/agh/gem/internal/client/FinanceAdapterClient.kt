package pl.edu.agh.gem.internal.client

import pl.edu.agh.gem.internal.model.Balances

interface FinanceAdapterClient {
    fun getGroupBalance(groupId: String): List<Balances>
}

class FinanceAdapterClientException(override val message: String?) : RuntimeException()

class RetryableFinanceAdapterClientException(override val message: String?) : RuntimeException()
