package pl.edu.agh.gem.internal.persistence

import pl.edu.agh.gem.internal.model.Group

interface GroupRepository {
    fun insertWithUniqueJoinCode(group: Group): Group
}
