package pl.edu.agh.gem.external.persistence

import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository
import pl.edu.agh.gem.internal.generator.CodeGenerator
import pl.edu.agh.gem.internal.model.Group
import pl.edu.agh.gem.internal.persistence.GroupRepository

@Repository
class MongoGroupRepository(
    private val mongo: MongoTemplate,
    private val codeGenerator: CodeGenerator,
) : GroupRepository {
    override fun insertWithUniqueJoinCode(group: Group): Group {
        val groupEntity = group.toEntity()
        return insertWithUniqueJoinCode(groupEntity)
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
