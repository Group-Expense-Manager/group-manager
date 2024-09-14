package pl.edu.agh.gem.internal.validation.wrapper

import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.validator.DataWrapper

interface CurrenciesDataWrapper : DataWrapper {
    val newCurrencies: Set<Currency>
}
