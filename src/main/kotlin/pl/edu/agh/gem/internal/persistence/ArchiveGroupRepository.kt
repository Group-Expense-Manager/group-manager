package pl.edu.agh.gem.internal.persistence

import pl.edu.agh.gem.internal.model.ArchiveGroup
import pl.edu.agh.gem.internal.model.Group

interface ArchiveGroupRepository {
    fun save(group: Group)

    fun findById(groupId: String): ArchiveGroup?
}
