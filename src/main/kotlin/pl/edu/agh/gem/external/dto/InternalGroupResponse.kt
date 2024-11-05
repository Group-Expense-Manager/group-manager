package pl.edu.agh.gem.external.dto

import pl.edu.agh.gem.internal.model.Group

data class InternalGroupResponse(
    val groupId: String,
    val name: String,
    val ownerId: String,
    val members: List<MemberDTO>,
    val groupCurrencies: List<CurrencyDTO>,
    val joinCode: String,
    val attachmentId: String,
)

fun Group.toInternalGroupResponse() = InternalGroupResponse(
    groupId = id,
    name = name,
    ownerId = ownerId,
    members = members.map { MemberDTO(it.userId) },
    groupCurrencies = currencies.map { CurrencyDTO(it.code) },
    joinCode = joinCode,
    attachmentId = attachmentId,
)

data class MemberDTO(
    val id: String,
)

data class CurrencyDTO(
    val code: String,
)
