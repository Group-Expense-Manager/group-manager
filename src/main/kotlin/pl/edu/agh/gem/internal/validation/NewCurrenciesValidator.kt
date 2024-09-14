package pl.edu.agh.gem.internal.validation

import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_NOT_BACKWARD_COMPATIBLE
import pl.edu.agh.gem.internal.validation.wrapper.GroupUpdateDataWrapper
import pl.edu.agh.gem.validator.BaseValidator
import pl.edu.agh.gem.validator.Check

class NewCurrenciesValidator : BaseValidator<GroupUpdateDataWrapper>() {

    override val checks: List<Check<GroupUpdateDataWrapper>> = listOf(
        Check(GROUP_CURRENCY_NOT_BACKWARD_COMPATIBLE) { this.backwardCompatibility(it) },
    )

    private fun backwardCompatibility(dataWrapper: GroupUpdateDataWrapper): Boolean {
        return dataWrapper.currencies.all { currency ->
            dataWrapper.newCurrencies.contains(currency)
        }
    }
}
