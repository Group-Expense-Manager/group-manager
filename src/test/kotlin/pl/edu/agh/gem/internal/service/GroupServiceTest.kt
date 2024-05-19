package pl.edu.agh.gem.internal.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import pl.edu.agh.gem.internal.client.CurrencyManagerClient
import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.persistence.GroupRepository
import pl.edu.agh.gem.util.createGroup
import pl.edu.agh.gem.validator.ValidatorsException

class GroupServiceTest : ShouldSpec({

    val currencyManagerClient = mock<CurrencyManagerClient>()
    val groupRepository = mock<GroupRepository>()
    val groupService = GroupService(currencyManagerClient, groupRepository)

    should("create group successfully") {
        // given
        val group = createGroup()
        val currencies = listOf("USD", "EUR", "PLN").map { Currency(it) }
        whenever(currencyManagerClient.getCurrencies()).thenReturn(currencies)
        whenever(groupRepository.insertWithUniqueJoinCode(group)).thenReturn(group)

        // when
        val result = groupService.createGroup(group)

        // then
        verify(groupRepository, times(1)).insertWithUniqueJoinCode(group)
        result shouldBe group
    }

    should("throw ValidatorsException when group creation fails") {
        // given
        val group = createGroup()
        val currencies = listOf("USD", "EUR", "CZK").map { Currency(it) }
        whenever(currencyManagerClient.getCurrencies()).thenReturn(currencies)

        // when & then
        shouldThrow<ValidatorsException> {
            groupService.createGroup(group)
        }
        verify(groupRepository, times(0)).insertWithUniqueJoinCode(group)
    }

    should("join group successfully") {
        // given
        val joinCode = "uniqueJoinCode"
        val userId = "userId"
        val group = createGroup(joinCode = joinCode)
        whenever(groupRepository.findByJoinCode(joinCode)).thenReturn(group)
        whenever(groupRepository.save(any())).thenReturn(group.copy(members = listOf(Member(userId))))

        // when
        val result = groupService.joinGroup(joinCode, userId)

        // then
        verify(groupRepository, times(1)).findByJoinCode(joinCode)
        verify(groupRepository, times(1)).save(any())
        result.members.map { it.userId } shouldContain userId
    }

    should("throw MissingGroupException when group with join code does not exist") {
        // given
        val joinCode = "nonExistentJoinCode"
        val userId = "userId"
        whenever(groupRepository.findByJoinCode(joinCode)).thenReturn(null)

        // when & then
        shouldThrow<MissingGroupException> {
            groupService.joinGroup(joinCode, userId)
        }
        verify(groupRepository, times(1)).findByJoinCode(joinCode)
        verify(groupRepository, times(0)).save(any())
    }

    should("throw UserAlreadyInGroupException when user is already in the group") {
        // given
        val joinCode = "uniqueJoinCode"
        val userId = "userId"
        val group = createGroup(joinCode = joinCode, members = listOf(Member(userId)))
        whenever(groupRepository.findByJoinCode(joinCode)).thenReturn(group)

        // when & then
        shouldThrow<UserAlreadyInGroupException> {
            groupService.joinGroup(joinCode, userId)
        }
        verify(groupRepository, times(1)).findByJoinCode(joinCode)
        verify(groupRepository, times(0)).save(any())
    }
},)
