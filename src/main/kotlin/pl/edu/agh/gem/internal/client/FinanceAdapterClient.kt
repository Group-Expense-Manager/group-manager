package pl.edu.agh.gem.internal.client

import pl.edu.agh.gem.internal.model.GroupBalance

interface FinanceAdapterClient {
    fun getGroupBalance(groupId: String): GroupBalance
}

class FinanceAdapterClientException(override val message: String?) : RuntimeException()

class RetryableFinanceAdapterClientException(override val message: String?) : RuntimeException()
