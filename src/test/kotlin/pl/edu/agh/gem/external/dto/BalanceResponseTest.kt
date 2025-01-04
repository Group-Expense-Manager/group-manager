package pl.edu.agh.gem.external.dto

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.util.createBalancesDto
import pl.edu.agh.gem.util.createBalancesResponse
import pl.edu.agh.gem.util.createUserBalanceDto
import java.math.BigDecimal

class BalanceResponseTest : ShouldSpec({

    should("map UserBalanceDto to domain") {
        // given
        val balanceDto = createUserBalanceDto()

        // when
        val userBalance = balanceDto.toDomain()

        // then
        userBalance.also {
            it.userId shouldBe USER_ID
            it.value shouldBe BigDecimal.ONE
        }
    }

    should("map BalancesDto to domain") {
        // given
        val balancesDto = createBalancesDto()

        // when
        val balances = balancesDto.toDomain()

        // then
        balances.also {
            it.currency shouldBe balancesDto.currency
            it.users shouldBe balancesDto.userBalances.map { userBalance -> userBalance.toDomain() }
        }
    }

    should("map BalancesResponse to domain") {
        // given
        val balancesResponse = createBalancesResponse()

        // when
        val balancesList = balancesResponse.toDomain()

        // then
        balancesList shouldBe balancesResponse.balances.map { currencyBalances -> currencyBalances.toDomain() }
    }
})
