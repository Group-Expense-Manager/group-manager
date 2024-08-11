package pl.edu.agh.gem.external.dto

import pl.edu.agh.gem.internal.model.Group

data class ExternalGroupResponse(
    val groupId: String,
    val name: String,
    val ownerId: String,
    val members: List<MemberDto>,
    val acceptRequired: Boolean,
    val groupCurrencies: List<CurrencyDto>,
    val joinCode: String,
    val attachmentId: String,
)

data class MemberDto(
    val userId: String,
)

data class CurrencyDto(
    val code: String,
)

fun Group.toExternalGroupResponse() = ExternalGroupResponse(
    groupId = id,
    name = name,
    ownerId = ownerId,
    members = members.map { MemberDto(it.userId) },
    acceptRequired = acceptRequired,
    groupCurrencies = currencies.map { CurrencyDto(it.code) },
    joinCode = joinCode,
    attachmentId = attachmentId,
)
