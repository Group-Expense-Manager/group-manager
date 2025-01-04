package pl.edu.agh.gem.internal.validation

import pl.edu.agh.gem.internal.validation.ValidationMessage.MISSING_PERMISSION
import pl.edu.agh.gem.internal.validation.ValidationMessage.USER_NOT_FOUND
import pl.edu.agh.gem.internal.validation.wrapper.MembersDataWrapper
import pl.edu.agh.gem.validator.BaseValidator
import pl.edu.agh.gem.validator.Check

class PermissionValidator : BaseValidator<MembersDataWrapper>() {
    override val checks: List<Check<MembersDataWrapper>> =
        listOf(
            Check(MISSING_PERMISSION) { checkPermission(it) },
            Check(USER_NOT_FOUND) { checkIfUserIsMember(it) },
        )

    private fun checkPermission(dataWrapper: MembersDataWrapper): Boolean {
        return dataWrapper.ownerId == dataWrapper.userId
    }

    private fun checkIfUserIsMember(dataWrapper: MembersDataWrapper): Boolean {
        return dataWrapper.members.any { it.userId == dataWrapper.userId }
    }
}
