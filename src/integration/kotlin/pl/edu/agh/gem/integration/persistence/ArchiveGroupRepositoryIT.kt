package pl.edu.agh.gem.integration.persistence

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.external.persistence.toArchiveEntity
import pl.edu.agh.gem.external.persistence.toDomain
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.internal.persistence.ArchiveGroupRepository
import pl.edu.agh.gem.util.createGroup

class ArchiveGroupRepositoryIT(
    private val archiveGroupRepository: ArchiveGroupRepository,
) : BaseIntegrationSpec({
    should("save archive group correctly") {
        // given
        val group = createGroup()

        // when
        archiveGroupRepository.save(group)

        // then
        val expectedArchivedGroup = group.toArchiveEntity().toDomain()
        val foundGroup = archiveGroupRepository.findById(group.id)
        foundGroup.shouldNotBeNull()
        foundGroup.shouldBe(expectedArchivedGroup)
    }

    should("find archive group by id") {
        // given
        val group = createGroup()
        archiveGroupRepository.save(group)

        // when
        val foundGroup = archiveGroupRepository.findById(group.id)

        // then
        val expectedArchivedGroup = group.toArchiveEntity().toDomain()
        foundGroup.shouldNotBeNull()
        foundGroup.shouldBe(expectedArchivedGroup)
    }

    should("return null if archive group with id does not exist") {
        // when
        val foundGroup = archiveGroupRepository.findById("nonExistentGroupId")

        // then
        foundGroup.shouldBeNull()
    }
},)
