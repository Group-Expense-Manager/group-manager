package pl.edu.agh.gem.internal.validation

import pl.edu.agh.gem.internal.model.Currencies
import pl.edu.agh.gem.internal.model.NewGroup
import pl.edu.agh.gem.validator.DataWrapper

data class NewGroupDataWrapper(
    val newGroup: NewGroup,
    val availableCurrencies: Currencies,
) : DataWrapper
