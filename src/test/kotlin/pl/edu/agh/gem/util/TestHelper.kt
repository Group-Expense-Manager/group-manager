package pl.edu.agh.gem.util

import pl.edu.agh.gem.external.dto.AvailableCurrenciesResponse
import pl.edu.agh.gem.external.dto.BalancesDto
import pl.edu.agh.gem.external.dto.BalancesResponse
import pl.edu.agh.gem.external.dto.CurrencyResponse
import pl.edu.agh.gem.external.dto.GroupAttachmentResponse
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
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.internal.model.ArchiveCurrency
import pl.edu.agh.gem.internal.model.ArchiveGroup
import pl.edu.agh.gem.internal.model.ArchiveMember
import pl.edu.agh.gem.internal.model.Balance
import pl.edu.agh.gem.internal.model.Balances
import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.internal.model.Group
import pl.edu.agh.gem.internal.model.GroupUpdate
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.model.NewGroup
import java.math.BigDecimal

fun createGroupCreationRequest(
    name: String = "groupName",
    groupCurrencies: List<GroupCreationCurrencyDto> = listOf(createGroupCreationCurrencyDto()),
) = GroupCreationRequest(
    name = name,
    groupCurrencies = groupCurrencies,
)

fun createGroupCreationCurrencyDto(code: String = "PLN") =
    GroupCreationCurrencyDto(
        code = code,
    )

fun createAvailableCurrenciesResponse(currencies: List<CurrencyResponse> = listOf(createCurrencyResponse())) =
    AvailableCurrenciesResponse(
        currencies = currencies,
    )

fun createGroupAttachmentResponse(attachmentId: String = "attachmentId") =
    GroupAttachmentResponse(
        id = attachmentId,
    )

fun createCurrencyResponse(code: String = "PLN") =
    CurrencyResponse(
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

fun createBalancesResponse(
    groupId: String = "groupId",
    balances: List<BalancesDto> = listOf(createBalancesDto()),
) = BalancesResponse(
    groupId = groupId,
    balances = balances,
)

fun createZeroBalancesResponse(
    groupId: String = "groupId",
    balances: List<BalancesDto> =
        listOf(
            createBalancesDto(
                currency = "PLN",
                userBalances =
                    listOf(
                        createUserBalanceDto(userId = USER_ID, "0".toBigDecimal()),
                        createUserBalanceDto(userId = OTHER_USER_ID, "0".toBigDecimal()),
                    ),
            ),
            createBalancesDto(
                currency = "EUR",
                userBalances =
                    listOf(
                        createUserBalanceDto(userId = USER_ID, "0".toBigDecimal()),
                        createUserBalanceDto(userId = OTHER_USER_ID, "0".toBigDecimal()),
                    ),
            ),
        ),
) = BalancesResponse(
    groupId = groupId,
    balances = balances,
)

fun createBalancesDto(
    currency: String = "PLN",
    userBalances: List<UserBalanceDto> =
        listOf(
            createUserBalanceDto(userId = USER_ID, "5".toBigDecimal()),
            createUserBalanceDto(userId = OTHER_USER_ID, "-2".toBigDecimal()),
        ),
) = BalancesDto(
    currency = currency,
    userBalances = userBalances,
)

fun createUserBalanceDto(
    userId: String = USER_ID,
    value: BigDecimal = "1".toBigDecimal(),
) = UserBalanceDto(
    userId = userId,
    value = value,
)

fun createBalances(
    currency: String = "PLN",
    users: List<Balance> =
        listOf(
            createBalance(userId = USER_ID, "5".toBigDecimal()),
            createBalance(userId = OTHER_USER_ID, "-2".toBigDecimal()),
        ),
) = Balances(
    currency = currency,
    users = users,
)

fun createZeroBalancesList(
    balancesList: List<Balances> =
        listOf(
            createBalances(
                currency = "PLN",
                users =
                    listOf(
                        createBalance(USER_ID, "0".toBigDecimal()),
                        createBalance(OTHER_USER_ID, "0".toBigDecimal()),
                    ),
            ),
            createBalances(
                currency = "EUR",
                users =
                    listOf(
                        createBalance(USER_ID, "0".toBigDecimal()),
                        createBalance(OTHER_USER_ID, "0".toBigDecimal()),
                    ),
            ),
        ),
) = balancesList

fun createBalance(
    userId: String = USER_ID,
    value: BigDecimal = "1".toBigDecimal(),
) = Balance(
    userId = userId,
    value = value,
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

fun createGroupUpdateCurrencyDto(code: String = "PLN") =
    GroupUpdateCurrencyDto(
        code = code,
    )
