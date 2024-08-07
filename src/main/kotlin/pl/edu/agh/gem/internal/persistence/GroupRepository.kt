package pl.edu.agh.gem.internal.persistence

import pl.edu.agh.gem.internal.model.Group

interface GroupRepository {
    fun insertWithUniqueJoinCode(group: Group): Group
    fun findByJoinCode(joinCode: String): Group?
    fun save(group: Group): Group
    fun findById(groupId: String): Group?
    fun findByUserId(userId: String): List<Group>
    fun remove(group: Group)
}
