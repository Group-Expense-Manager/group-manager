package pl.edu.agh.gem.internal.validation

import pl.edu.agh.gem.internal.validation.ValidationMessage.BASE_CURRENCY_NOT_EMPTY
import pl.edu.agh.gem.internal.validation.ValidationMessage.BASE_CURRENCY_NOT_SUPPORTED
import pl.edu.agh.gem.validator.BaseValidator
import pl.edu.agh.gem.validator.Check

class CurrenciesValidator : BaseValidator<GroupDataWrapper>() {

    override val checks: List<Check<GroupDataWrapper>> = listOf(
        Check(BASE_CURRENCY_NOT_SUPPORTED) { this.validateCurrency(it) },
        Check(BASE_CURRENCY_NOT_EMPTY) { it.group.baseCurrency.isNotEmpty() },
    )

    private fun validateCurrency(dataWrapper: GroupDataWrapper): Boolean {
        return dataWrapper.group.baseCurrency.all { currency ->
            dataWrapper.availableCurrencies.contains(currency)
        }
    }
}
