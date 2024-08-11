package pl.edu.agh.gem.external.persitence

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.external.persistence.CurrencyEntity
import pl.edu.agh.gem.external.persistence.MemberEntity
import pl.edu.agh.gem.external.persistence.toDomain
import pl.edu.agh.gem.external.persistence.toEntity
import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.util.createGroup
import pl.edu.agh.gem.util.createGroupEntity

class GroupEntityTest : ShouldSpec({

    should("map correct GroupEntity to Group") {
        // given
        val group = createGroup()

        // when
        val groupEntity = group.toEntity()

        // then
        groupEntity.also {
            it.id shouldBe group.id
            it.name shouldBe group.name
            it.ownerId shouldBe group.ownerId
            it.members shouldBe group.members.map { member -> MemberEntity(member.userId) }
            it.acceptRequired shouldBe group.acceptRequired
            it.groupCurrencies shouldBe group.currencies.map { currency -> CurrencyEntity(currency.code) }
            it.joinCode shouldBe group.joinCode
            it.attachmentId shouldBe group.attachmentId
        }
    }

    should("map correct Group to GroupEntity") {
        // given
        val groupEntity = createGroupEntity()

        // when
        val group = groupEntity.toDomain()

        // then
        group.also {
            it.id shouldBe groupEntity.id
            it.name shouldBe groupEntity.name
            it.ownerId shouldBe groupEntity.ownerId
            it.members shouldBe groupEntity.members.map { memberEntity -> Member(memberEntity.userId) }
            it.acceptRequired shouldBe groupEntity.acceptRequired
            it.currencies shouldBe groupEntity.groupCurrencies.map { currencyEntity -> Currency(currencyEntity.code) }
            it.joinCode shouldBe groupEntity.joinCode
            it.attachmentId shouldBe groupEntity.attachmentId
        }
    }
},)
