package pl.edu.agh.gem.internal.validation

import pl.edu.agh.gem.internal.client.FinanceAdapterClient
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_BALANCE_NOT_ZERO
import pl.edu.agh.gem.validator.BaseValidator
import pl.edu.agh.gem.validator.Check
import java.math.BigDecimal.ZERO

class BalanceValidator(
    private val financeAdapterClient: FinanceAdapterClient,
) : BaseValidator<GroupDataWrapper>() {

    override val checks: List<Check<GroupDataWrapper>> = listOf(
        Check(GROUP_BALANCE_NOT_ZERO) { checkBalance(it) },
    )

    private fun checkBalance(dataWrapper: GroupDataWrapper): Boolean {
        val groupBalance = financeAdapterClient.getGroupBalance(dataWrapper.group.id)
        return groupBalance.usersBalance.flatMap { userBalance -> userBalance.balance.map { it.amount } }.all { it == ZERO }
    }
}
