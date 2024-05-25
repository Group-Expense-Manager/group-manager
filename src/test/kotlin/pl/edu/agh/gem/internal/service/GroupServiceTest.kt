package pl.edu.agh.gem.internal.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import pl.edu.agh.gem.internal.client.CurrencyManagerClient
import pl.edu.agh.gem.internal.model.Currency
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
},)
