package pl.edu.agh.gem.external.dto

import pl.edu.agh.gem.internal.model.Balance
import pl.edu.agh.gem.internal.model.Balances
import java.math.BigDecimal

data class BalancesResponse(
    val groupId: String,
    val balances: List<BalancesDto>,
) {
    fun toDomain() = balances.map { it.toDomain() }
}

data class BalancesDto(
    val currency: String,
    val userBalances: List<UserBalanceDto>,
) {
    fun toDomain() =
        Balances(
            currency = currency,
            users = userBalances.map { it.toDomain() },
        )
}

data class UserBalanceDto(
    val userId: String,
    val value: BigDecimal,
) {
    fun toDomain() =
        Balance(
            userId = userId,
            value = value,
        )
}
