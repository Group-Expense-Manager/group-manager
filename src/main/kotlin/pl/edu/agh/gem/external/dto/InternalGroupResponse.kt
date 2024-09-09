package pl.edu.agh.gem.external.dto

import pl.edu.agh.gem.internal.model.Group

data class InternalGroupResponse(
    val members: List<MemberDTO>,
    val groupCurrencies: List<CurrencyDTO>,
)

fun Group.toInternalGroupResponse() = InternalGroupResponse(
    members = members.map { MemberDTO(it.userId) },
    groupCurrencies = currencies.map { CurrencyDTO(it.code) },
)

data class MemberDTO(
    val id: String,
)

data class CurrencyDTO(
    val code: String,
)
