package pl.edu.agh.gem.util

import pl.edu.agh.gem.external.dto.AvailableCurrenciesResponse
import pl.edu.agh.gem.external.dto.CurrencyResponse
import pl.edu.agh.gem.external.dto.GroupCreationRequest
import pl.edu.agh.gem.external.persistence.CurrencyEntity
import pl.edu.agh.gem.external.persistence.GroupEntity
import pl.edu.agh.gem.external.persistence.MemberEntity
import pl.edu.agh.gem.internal.model.Currencies
import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.internal.model.Group
import pl.edu.agh.gem.internal.model.Member

fun createGroupCreationRequest(
    name: String = "groupName",
    color: Long = 0,
    acceptRequired: Boolean = false,
    groupCurrencies: String = "PLN",
    attachmentId: String = "attachmentId",
) = GroupCreationRequest(
    name = name,
    color = color,
    acceptRequired = acceptRequired,
    groupCurrencies = groupCurrencies,
    attachmentId = attachmentId,
)

fun createAvailableCurrenciesResponse(
    currencies: List<CurrencyResponse> = listOf(createCurrencyResponse()),
) = AvailableCurrenciesResponse(
    currencies = currencies,
)

fun createCurrencyResponse(
    code: String = "PLN",
) = CurrencyResponse(
    code = code,
)

fun createGroup(
    id: String = "groupId",
    name: String = "groupName",
    color: Long = 0,
    ownerId: String = "ownerId",
    members: List<Member> = listOf(Member("memberId")),
    acceptRequired: Boolean = false,
    groupCurrencies: Currencies = listOf(Currency("PLN")),
    attachmentId: String = "attachmentId",
    joinCode: String = "joinCode",
) = Group(
    id = id,
    name = name,
    color = color,
    ownerId = ownerId,
    members = members,
    acceptRequired = acceptRequired,
    groupCurrencies = groupCurrencies,
    attachmentId = attachmentId,
    joinCode = joinCode,
)

fun createGroupEntity(
    id: String = "groupId",
    name: String = "groupName",
    color: Long = 0,
    ownerId: String = "ownerId",
    members: List<MemberEntity> = listOf(MemberEntity("memberId")),
    acceptRequired: Boolean = false,
    groupCurrencies: List<CurrencyEntity> = listOf(CurrencyEntity("PLN")),
    attachmentId: String = "attachmentId",
    joinCode: String = "joinCode",
) = GroupEntity(
    id = id,
    name = name,
    color = color,
    ownerId = ownerId,
    members = members,
    acceptRequired = acceptRequired,
    groupCurrencies = groupCurrencies,
    attachmentId = attachmentId,
    joinCode = joinCode,
)
