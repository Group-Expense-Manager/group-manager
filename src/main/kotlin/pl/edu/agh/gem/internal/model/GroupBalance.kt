package pl.edu.agh.gem.internal.model

import java.math.BigDecimal

data class GroupBalance(
    val id: String,
    val usersBalance: List<UserBalance>,
)

data class UserBalance(
    val userId: String,
    val balance: List<Balance>,
)

data class Balance(
    val currency: String,
    val amount: BigDecimal,
)
