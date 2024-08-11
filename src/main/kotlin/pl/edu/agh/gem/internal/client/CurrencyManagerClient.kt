package pl.edu.agh.gem.internal.client

import pl.edu.agh.gem.internal.model.Currency

interface CurrencyManagerClient {
    fun getCurrencies(): List<Currency>
}

class CurrencyManagerClientException(override val message: String?) : RuntimeException()

class RetryableCurrencyManagerClientException(override val message: String?) : RuntimeException()
