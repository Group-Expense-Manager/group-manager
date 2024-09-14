package pl.edu.agh.gem.internal.validation.wrapper

import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.internal.model.Member

data class GroupUpdateDataWrapper(
    override val userId: String,
    override val members: Set<Member>,
    override val ownerId: String,
    override val newCurrencies: Set<Currency>,
    val currencies: Set<Currency>,
) : MembersDataWrapper, CurrenciesDataWrapper
