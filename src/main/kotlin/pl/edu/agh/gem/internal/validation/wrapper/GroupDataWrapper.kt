package pl.edu.agh.gem.internal.validation.wrapper

import pl.edu.agh.gem.internal.model.Member

data class GroupDataWrapper(
    override val groupId: String,
    override val userId: String,
    override val members: Set<Member>,
    override val ownerId: String,
) : MembersDataWrapper, GroupIdentifierDataWrapper
