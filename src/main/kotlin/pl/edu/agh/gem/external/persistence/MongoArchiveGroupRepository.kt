package pl.edu.agh.gem.external.persistence

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Repository
import pl.edu.agh.gem.internal.model.ArchiveGroup
import pl.edu.agh.gem.internal.model.Group
import pl.edu.agh.gem.internal.persistence.ArchiveGroupRepository
import pl.edu.agh.gem.metrics.MeteredRepository

@Repository
@MeteredRepository
class MongoArchiveGroupRepository(
    private val mongo: MongoTemplate,
) : ArchiveGroupRepository {
    override fun save(group: Group) {
        mongo.save(group.toArchiveEntity())
    }

    override fun findById(groupId: String): ArchiveGroup? {
        val query = query(where(ArchiveGroupEntity::id).isEqualTo(groupId))
        return mongo.findOne(query, ArchiveGroupEntity::class.java)?.toDomain()
    }
}
