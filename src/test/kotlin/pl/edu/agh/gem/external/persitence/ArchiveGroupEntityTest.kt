package pl.edu.agh.gem.external.persitence

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.external.persistence.ArchiveCurrencyEntity
import pl.edu.agh.gem.external.persistence.ArchiveMemberEntity
import pl.edu.agh.gem.external.persistence.toArchiveEntity
import pl.edu.agh.gem.external.persistence.toDomain
import pl.edu.agh.gem.external.persistence.toEntity
import pl.edu.agh.gem.internal.model.ArchiveCurrency
import pl.edu.agh.gem.internal.model.ArchiveMember
import pl.edu.agh.gem.util.createArchiveGroup
import pl.edu.agh.gem.util.createArchiveGroupEntity
import pl.edu.agh.gem.util.createGroup

class ArchiveGroupEntityTest : ShouldSpec({

    should("map correct Group to ArchiveGroupEntity") {
        // given
        val group = createGroup()

        // when
        val archiveGroupEntity = group.toArchiveEntity()

        // then
        archiveGroupEntity.also {
            it.id shouldBe group.id
            it.name shouldBe group.name
            it.ownerId shouldBe group.ownerId
            it.members shouldBe group.members.map { member -> ArchiveMemberEntity(member.userId) }
            it.groupCurrencies shouldBe group.currencies.map { currency -> ArchiveCurrencyEntity(currency.code) }
            it.attachmentId shouldBe group.attachmentId
        }
    }

    should("map correct ArchiveGroupEntity to ArchiveGroup") {
        // given
        val archiveGroupEntity = createArchiveGroupEntity()

        // when
        val archiveGroup = archiveGroupEntity.toDomain()

        // then
        archiveGroup.also {
            it.id shouldBe archiveGroupEntity.id
            it.name shouldBe archiveGroupEntity.name
            it.ownerId shouldBe archiveGroupEntity.ownerId
            it.members shouldBe archiveGroupEntity.members.map { memberEntity -> ArchiveMember(memberEntity.userId) }
            it.groupCurrencies shouldBe archiveGroupEntity.groupCurrencies.map { currencyEntity -> ArchiveCurrency(currencyEntity.code) }
            it.attachmentId shouldBe archiveGroupEntity.attachmentId
        }
    }

    should("map correct ArchiveGroup to ArchiveGroupEntity") {
        // given
        val archiveGroup = createArchiveGroup()

        // when
        val archiveGroupEntity = archiveGroup.toEntity()

        // then
        archiveGroupEntity.also {
            it.id shouldBe archiveGroup.id
            it.name shouldBe archiveGroup.name
            it.ownerId shouldBe archiveGroup.ownerId
            it.members shouldBe archiveGroup.members.map { member -> ArchiveMemberEntity(member.userId) }
            it.groupCurrencies shouldBe archiveGroup.groupCurrencies.map { currency -> ArchiveCurrencyEntity(currency.code) }
            it.attachmentId shouldBe archiveGroup.attachmentId
        }
    }
})
