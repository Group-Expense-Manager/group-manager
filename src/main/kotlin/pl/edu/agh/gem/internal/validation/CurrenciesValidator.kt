package pl.edu.agh.gem.internal.validation

import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_NOT_EMPTY
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_NOT_SUPPORTED
import pl.edu.agh.gem.validator.BaseValidator
import pl.edu.agh.gem.validator.Check

class CurrenciesValidator : BaseValidator<NewGroupDataWrapper>() {

    override val checks: List<Check<NewGroupDataWrapper>> = listOf(
        Check(GROUP_CURRENCY_NOT_SUPPORTED) { this.validateCurrency(it) },
        Check(GROUP_CURRENCY_NOT_EMPTY) { it.newGroup.groupCurrencies.isNotEmpty() },
    )

    private fun validateCurrency(dataWrapper: NewGroupDataWrapper): Boolean {
        return dataWrapper.newGroup.groupCurrencies.all { currency ->
            dataWrapper.availableCurrencies.contains(currency)
        }
    }
}
