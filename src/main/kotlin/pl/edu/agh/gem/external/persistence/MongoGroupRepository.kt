package pl.edu.agh.gem.external.persistence

import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Repository
import pl.edu.agh.gem.internal.generator.CodeGenerator
import pl.edu.agh.gem.internal.model.Group
import pl.edu.agh.gem.internal.persistence.GroupRepository
import pl.edu.agh.gem.metrics.MeteredRepository

@Repository
@MeteredRepository
class MongoGroupRepository(
    private val mongo: MongoTemplate,
    private val codeGenerator: CodeGenerator,
) : GroupRepository {
    override fun insertWithUniqueJoinCode(group: Group): Group {
        val groupEntity = group.toEntity()
        return insertWithUniqueJoinCode(groupEntity)
    }

    override fun findByJoinCode(joinCode: String): Group? {
        val query = query(where(GroupEntity::joinCode).isEqualTo(joinCode))
        return mongo.findOne(query, GroupEntity::class.java)?.toDomain()
    }

    override fun save(group: Group): Group {
        return mongo.save(group.toEntity()).toDomain()
    }

    override fun findById(groupId: String): Group? {
        return mongo.findById(groupId, GroupEntity::class.java)?.toDomain()
    }

    override fun findByUserId(userId: String): List<Group> {
        val query = query(where(GroupEntity::members).elemMatch(where(MemberEntity::userId).isEqualTo(userId)))
        return mongo.find(query, GroupEntity::class.java).map(GroupEntity::toDomain)
    }

    override fun remove(group: Group) {
        val query = query(where(GroupEntity::id).isEqualTo(group.id))
        mongo.remove(query, GroupEntity::class.java)
    }

    private fun insertWithUniqueJoinCode(groupEntity: GroupEntity): Group {
        return try {
            mongo.insert(groupEntity).toDomain()
        } catch (e: DuplicateKeyException) {
            val newGroupEntity = groupEntity.copy(joinCode = codeGenerator.generateJoinCode())
            insertWithUniqueJoinCode(newGroupEntity)
        }
    }
}
