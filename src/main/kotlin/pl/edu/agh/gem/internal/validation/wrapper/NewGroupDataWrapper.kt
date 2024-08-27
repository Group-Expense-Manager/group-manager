package pl.edu.agh.gem.internal.validation.wrapper

import pl.edu.agh.gem.internal.model.Currency

data class NewGroupDataWrapper(
    override val currencies: Set<Currency>,
) : CurrenciesDataWrapper
