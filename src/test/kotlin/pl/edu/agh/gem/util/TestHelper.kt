package pl.edu.agh.gem.util

import pl.edu.agh.gem.external.dto.AvailableCurrenciesResponse
import pl.edu.agh.gem.external.dto.CurrencyResponse
import pl.edu.agh.gem.external.dto.GroupAttachmentResponse
import pl.edu.agh.gem.external.dto.GroupCreationRequest
import pl.edu.agh.gem.external.persistence.CurrencyEntity
import pl.edu.agh.gem.external.persistence.GroupEntity
import pl.edu.agh.gem.external.persistence.MemberEntity
import pl.edu.agh.gem.internal.model.Currencies
import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.internal.model.Group
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.model.NewGroup

fun createGroupCreationRequest(
    name: String = "groupName",
    acceptRequired: Boolean = false,
    groupCurrencies: String = "PLN",
) = GroupCreationRequest(
    name = name,
    acceptRequired = acceptRequired,
    groupCurrencies = groupCurrencies,
)

fun createAvailableCurrenciesResponse(
    currencies: List<CurrencyResponse> = listOf(createCurrencyResponse()),
) = AvailableCurrenciesResponse(
    currencies = currencies,
)

fun createGroupAttachmentResponse(
    attachmentId: String = "attachmentId",
) = GroupAttachmentResponse(
    id = attachmentId,
)

fun createCurrencyResponse(
    code: String = "PLN",
) = CurrencyResponse(
    code = code,
)

fun createGroup(
    id: String = "groupId",
    name: String = "groupName",
    ownerId: String = "ownerId",
    members: List<Member> = listOf(Member("memberId")),
    acceptRequired: Boolean = false,
    groupCurrencies: Currencies = listOf(Currency("PLN")),
    attachmentId: String = "attachmentId",
    joinCode: String = "joinCode",
) = Group(
    id = id,
    name = name,
    ownerId = ownerId,
    members = members,
    acceptRequired = acceptRequired,
    groupCurrencies = groupCurrencies,
    attachmentId = attachmentId,
    joinCode = joinCode,
)

fun createNewGroup(
    id: String = "groupId",
    name: String = "groupName",
    ownerId: String = "ownerId",
    members: List<Member> = listOf(Member("memberId")),
    acceptRequired: Boolean = false,
    groupCurrencies: Currencies = listOf(Currency("PLN")),
) = NewGroup(
    id = id,
    name = name,
    ownerId = ownerId,
    members = members,
    acceptRequired = acceptRequired,
    groupCurrencies = groupCurrencies,
)

fun createGroupEntity(
    id: String = "groupId",
    name: String = "groupName",
    ownerId: String = "ownerId",
    members: List<MemberEntity> = listOf(MemberEntity("memberId")),
    acceptRequired: Boolean = false,
    groupCurrencies: List<CurrencyEntity> = listOf(CurrencyEntity("PLN")),
    attachmentId: String = "attachmentId",
    joinCode: String = "joinCode",
) = GroupEntity(
    id = id,
    name = name,
    ownerId = ownerId,
    members = members,
    acceptRequired = acceptRequired,
    groupCurrencies = groupCurrencies,
    attachmentId = attachmentId,
    joinCode = joinCode,
)
