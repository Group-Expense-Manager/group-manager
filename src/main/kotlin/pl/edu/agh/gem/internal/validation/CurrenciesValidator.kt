package pl.edu.agh.gem.internal.validation

import pl.edu.agh.gem.internal.client.CurrencyManagerClient
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_NOT_SUPPORTED
import pl.edu.agh.gem.internal.validation.wrapper.CurrenciesDataWrapper
import pl.edu.agh.gem.validator.BaseValidator
import pl.edu.agh.gem.validator.Check

class CurrenciesValidator(
    private val currencyManagerClient: CurrencyManagerClient,
) : BaseValidator<CurrenciesDataWrapper>() {

    override val checks: List<Check<CurrenciesDataWrapper>> = listOf(
        Check(GROUP_CURRENCY_NOT_SUPPORTED) { this.validateCurrency(it) },
    )

    private fun validateCurrency(dataWrapper: CurrenciesDataWrapper): Boolean {
        val availableCurrencies = currencyManagerClient.getCurrencies().toSet()
        return dataWrapper.newCurrencies.all { currency ->
            availableCurrencies.contains(currency)
        }
    }
}
