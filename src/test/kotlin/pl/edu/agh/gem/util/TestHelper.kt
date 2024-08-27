package pl.edu.agh.gem.util

import pl.edu.agh.gem.external.dto.AvailableCurrenciesResponse
import pl.edu.agh.gem.external.dto.BalanceDto
import pl.edu.agh.gem.external.dto.CurrencyResponse
import pl.edu.agh.gem.external.dto.GroupAttachmentResponse
import pl.edu.agh.gem.external.dto.GroupBalanceResponse
import pl.edu.agh.gem.external.dto.GroupCreationRequest
import pl.edu.agh.gem.external.dto.GroupUpdateCurrencyDto
import pl.edu.agh.gem.external.dto.GroupUpdateRequest
import pl.edu.agh.gem.external.dto.UserBalanceDto
import pl.edu.agh.gem.external.persistence.ArchiveCurrencyEntity
import pl.edu.agh.gem.external.persistence.ArchiveGroupEntity
import pl.edu.agh.gem.external.persistence.ArchiveMemberEntity
import pl.edu.agh.gem.external.persistence.CurrencyEntity
import pl.edu.agh.gem.external.persistence.GroupEntity
import pl.edu.agh.gem.external.persistence.MemberEntity
import pl.edu.agh.gem.internal.model.ArchiveCurrency
import pl.edu.agh.gem.internal.model.ArchiveGroup
import pl.edu.agh.gem.internal.model.ArchiveMember
import pl.edu.agh.gem.internal.model.Balance
import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.internal.model.Group
import pl.edu.agh.gem.internal.model.GroupBalance
import pl.edu.agh.gem.internal.model.GroupUpdate
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.model.NewGroup
import pl.edu.agh.gem.internal.model.UserBalance
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

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
    members: Set<Member> = setOf(Member("memberId")),
    acceptRequired: Boolean = false,
    currencies: Set<Currency> = setOf(Currency("PLN")),
    attachmentId: String = "attachmentId",
    joinCode: String = "joinCode",
) = Group(
    id = id,
    name = name,
    ownerId = ownerId,
    members = members,
    acceptRequired = acceptRequired,
    currencies = currencies,
    attachmentId = attachmentId,
    joinCode = joinCode,
)

fun createNewGroup(
    id: String = "groupId",
    name: String = "groupName",
    ownerId: String = "ownerId",
    members: Set<Member> = setOf(Member("memberId")),
    acceptRequired: Boolean = false,
    groupCurrencies: Set<Currency> = setOf(Currency("PLN")),
) = NewGroup(
    id = id,
    name = name,
    ownerId = ownerId,
    members = members,
    acceptRequired = acceptRequired,
    currencies = groupCurrencies,
)

fun createGroupEntity(
    id: String = "groupId",
    name: String = "groupName",
    ownerId: String = "ownerId",
    members: Set<MemberEntity> = setOf(MemberEntity("memberId")),
    acceptRequired: Boolean = false,
    groupCurrencies: Set<CurrencyEntity> = setOf(CurrencyEntity("PLN")),
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

fun createGroupBalanceResponse(
    groupId: String = "groupId",
    usersBalance: List<UserBalanceDto> = listOf(createUserBalanceDto()),
) = GroupBalanceResponse(
    id = groupId,
    usersBalance = usersBalance,
)

fun createUserBalanceDto(
    userId: String = "userId",
    balance: List<BalanceDto> = listOf(createBalanceDto()),
) = UserBalanceDto(
    userId = userId,
    balance = balance,
)

fun createBalanceDto(
    currency: String = "PLN",
    amount: BigDecimal = ZERO,
) = BalanceDto(
    currency = currency,
    amount = amount,
)

fun createGroupBalance(
    groupId: String = "groupId",
    usersBalance: List<UserBalance> = listOf(createUserBalance()),
) = GroupBalance(
    id = groupId,
    usersBalance = usersBalance,
)

fun createUserBalance(
    userId: String = "userId",
    balance: List<Balance> = listOf(createBalance()),
) = UserBalance(
    userId = userId,
    balance = balance,
)

fun createBalance(
    currency: String = "PLN",
    amount: BigDecimal = ZERO,
) = Balance(
    currency = currency,
    amount = amount,
)

fun createArchiveGroupEntity(
    id: String = "groupId",
    name: String = "groupName",
    ownerId: String = "ownerId",
    members: List<ArchiveMemberEntity> = listOf(ArchiveMemberEntity("memberId")),
    acceptRequired: Boolean = false,
    groupCurrencies: List<ArchiveCurrencyEntity> = listOf(ArchiveCurrencyEntity("PLN")),
    attachmentId: String = "attachmentId",
) = ArchiveGroupEntity(
    id = id,
    name = name,
    ownerId = ownerId,
    members = members,
    acceptRequired = acceptRequired,
    groupCurrencies = groupCurrencies,
    attachmentId = attachmentId,
)

fun createArchiveGroup(
    id: String = "groupId",
    name: String = "groupName",
    ownerId: String = "ownerId",
    members: List<ArchiveMember> = listOf(ArchiveMember("memberId")),
    acceptRequired: Boolean = false,
    groupCurrencies: List<ArchiveCurrency> = listOf(ArchiveCurrency("PLN")),
    attachmentId: String = "attachmentId",
) = ArchiveGroup(
    id = id,
    name = name,
    ownerId = ownerId,
    members = members,
    acceptRequired = acceptRequired,
    groupCurrencies = groupCurrencies,
    attachmentId = attachmentId,
)

fun createGroupUpdate(
    id: String = "groupId",
    name: String = "groupName",
    acceptRequired: Boolean = false,
    currencies: Set<Currency> = setOf(Currency("PLN")),
) = GroupUpdate(
    id = id,
    name = name,
    acceptRequired = acceptRequired,
    currencies = currencies,
)

fun createGroupUpdateRequest(
    name: String = "groupName",
    acceptRequired: Boolean = false,
    groupCurrencies: List<GroupUpdateCurrencyDto> = listOf(createGroupUpdateCurrencyDto()),
) = GroupUpdateRequest(
    name = name,
    acceptRequired = acceptRequired,
    groupCurrencies = groupCurrencies,
)

fun createGroupUpdateCurrencyDto(
    code: String = "PLN",
) = GroupUpdateCurrencyDto(
    code = code,
)
