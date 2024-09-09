package pl.edu.agh.gem.util

import pl.edu.agh.gem.external.dto.AvailableCurrenciesResponse
import pl.edu.agh.gem.external.dto.BalanceDto
import pl.edu.agh.gem.external.dto.CurrencyResponse
import pl.edu.agh.gem.external.dto.GroupAttachmentResponse
import pl.edu.agh.gem.external.dto.GroupBalanceResponse
import pl.edu.agh.gem.external.dto.GroupCreationCurrencyDto
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
    groupCurrencies: List<GroupCreationCurrencyDto> = listOf(createGroupCreationCurrencyDto()),
) = GroupCreationRequest(
    name = name,
    groupCurrencies = groupCurrencies,
)

fun createGroupCreationCurrencyDto(
    code: String = "PLN",
) = GroupCreationCurrencyDto(
    code = code,
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
    currencies: Set<Currency> = setOf(Currency("PLN")),
    attachmentId: String = "attachmentId",
    joinCode: String = "joinCode",
) = Group(
    id = id,
    name = name,
    ownerId = ownerId,
    members = members,
    currencies = currencies,
    attachmentId = attachmentId,
    joinCode = joinCode,
)

fun createNewGroup(
    id: String = "groupId",
    name: String = "groupName",
    ownerId: String = "ownerId",
    members: Set<Member> = setOf(Member("memberId")),
    groupCurrencies: Set<Currency> = setOf(Currency("PLN")),
) = NewGroup(
    id = id,
    name = name,
    ownerId = ownerId,
    members = members,
    currencies = groupCurrencies,
)

fun createGroupEntity(
    id: String = "groupId",
    name: String = "groupName",
    ownerId: String = "ownerId",
    members: Set<MemberEntity> = setOf(MemberEntity("memberId")),
    groupCurrencies: Set<CurrencyEntity> = setOf(CurrencyEntity("PLN")),
    attachmentId: String = "attachmentId",
    joinCode: String = "joinCode",
) = GroupEntity(
    id = id,
    name = name,
    ownerId = ownerId,
    members = members,
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
    groupCurrencies: List<ArchiveCurrencyEntity> = listOf(ArchiveCurrencyEntity("PLN")),
    attachmentId: String = "attachmentId",
) = ArchiveGroupEntity(
    id = id,
    name = name,
    ownerId = ownerId,
    members = members,
    groupCurrencies = groupCurrencies,
    attachmentId = attachmentId,
)

fun createArchiveGroup(
    id: String = "groupId",
    name: String = "groupName",
    ownerId: String = "ownerId",
    members: List<ArchiveMember> = listOf(ArchiveMember("memberId")),
    groupCurrencies: List<ArchiveCurrency> = listOf(ArchiveCurrency("PLN")),
    attachmentId: String = "attachmentId",
) = ArchiveGroup(
    id = id,
    name = name,
    ownerId = ownerId,
    members = members,
    groupCurrencies = groupCurrencies,
    attachmentId = attachmentId,
)

fun createGroupUpdate(
    id: String = "groupId",
    name: String = "groupName",
    currencies: Set<Currency> = setOf(Currency("PLN")),
) = GroupUpdate(
    id = id,
    name = name,
    currencies = currencies,
)

fun createGroupUpdateRequest(
    name: String = "groupName",
    groupCurrencies: List<GroupUpdateCurrencyDto> = listOf(createGroupUpdateCurrencyDto()),
) = GroupUpdateRequest(
    name = name,
    groupCurrencies = groupCurrencies,
)

fun createGroupUpdateCurrencyDto(
    code: String = "PLN",
) = GroupUpdateCurrencyDto(
    code = code,
)
