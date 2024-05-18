package pl.edu.agh.gem.internal.validation

import pl.edu.agh.gem.internal.model.Currencies
import pl.edu.agh.gem.internal.model.Group
import pl.edu.agh.gem.validator.DataWrapper

data class GroupDataWrapper(
    val group: Group,
    val availableCurrencies: Currencies,
) : DataWrapper
