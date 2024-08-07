package pl.edu.agh.gem.external.dto

import pl.edu.agh.gem.internal.model.Balance
import pl.edu.agh.gem.internal.model.GroupBalance
import pl.edu.agh.gem.internal.model.UserBalance
import java.math.BigDecimal

data class GroupBalanceResponse(
    val id: String,
    val usersBalance: List<UserBalanceDto>,
)

data class UserBalanceDto(
    val userId: String,
    val balance: List<BalanceDto>,
)

data class BalanceDto(
    val currency: String,
    val amount: BigDecimal,
)

fun GroupBalanceResponse.toDomain() = GroupBalance(
    id = id,
    usersBalance = usersBalance.map { it.toDomain() },
)

fun UserBalanceDto.toDomain() = UserBalance(
    userId = userId,
    balance = balance.map { it.toDomain() },
)

fun BalanceDto.toDomain() = Balance(
    currency = currency,
    amount = amount,
)
