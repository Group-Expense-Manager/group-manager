package pl.edu.agh.gem.internal.validation

import pl.edu.agh.gem.internal.client.FinanceAdapterClient
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_BALANCE_NOT_ZERO
import pl.edu.agh.gem.internal.validation.wrapper.GroupIdentifierDataWrapper
import pl.edu.agh.gem.validator.BaseValidator
import pl.edu.agh.gem.validator.Check
import java.math.BigDecimal.ZERO

class BalanceValidator(
    private val financeAdapterClient: FinanceAdapterClient,
) : BaseValidator<GroupIdentifierDataWrapper>() {
    override val checks: List<Check<GroupIdentifierDataWrapper>> =
        listOf(
            Check(GROUP_BALANCE_NOT_ZERO) { checkBalance(it) },
        )

    private fun checkBalance(dataWrapper: GroupIdentifierDataWrapper): Boolean {
        val balancesList = financeAdapterClient.getGroupBalance(dataWrapper.groupId)
        return balancesList.flatMap { balances -> balances.users.map { it.value } }
            .all { it.compareTo(ZERO) == 0 }
    }
}
