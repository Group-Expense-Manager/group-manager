package pl.edu.agh.gem.internal.client

import pl.edu.agh.gem.internal.model.Currencies

interface CurrencyManagerClient {
    fun getCurrencies(): Currencies
}

class CurrencyManagerClientException(override val message: String?) : RuntimeException()

class RetryableCurrencyManagerClientException(override val message: String?) : RuntimeException()
