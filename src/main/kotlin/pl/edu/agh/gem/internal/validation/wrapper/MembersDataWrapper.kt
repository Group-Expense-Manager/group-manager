package pl.edu.agh.gem.internal.validation.wrapper

import pl.edu.agh.gem.internal.model.Member

interface MembersDataWrapper : UserActionDataWrapper {
    override val userId: String
    val members: Set<Member>
    val ownerId: String
}
