package pl.edu.agh.gem.external.dto

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.util.createGroupBalanceResponse

class GroupBalanceResponseTest : ShouldSpec({

    should("map correct GroupBalanceResponse to GroupBalance") {
        // given
        val groupBalanceResponse = createGroupBalanceResponse()

        // when
        val groupBalance = groupBalanceResponse.toDomain()

        // then
        groupBalance.id shouldBe groupBalanceResponse.id

        val userBalancesMap = groupBalance.usersBalance.associateBy { it.userId }
        val userBalancesDtoMap = groupBalanceResponse.usersBalance.associateBy { it.userId }

        userBalancesDtoMap.keys shouldContainExactly userBalancesMap.keys

        userBalancesDtoMap.forEach { (userId, userBalanceDto) ->
            val userBalance = userBalancesMap[userId]?.balance?.map { Pair(it.currency, it.amount) } ?: listOf()
            userBalanceDto.balance.map { Pair(it.currency, it.amount) } shouldContainExactly userBalance
        }
    }
},)
