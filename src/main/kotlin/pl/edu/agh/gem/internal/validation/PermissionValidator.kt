package pl.edu.agh.gem.internal.validation

import pl.edu.agh.gem.internal.validation.ValidationMessage.MISSING_PERMISSION
import pl.edu.agh.gem.validator.BaseValidator
import pl.edu.agh.gem.validator.Check

class PermissionValidator : BaseValidator<GroupDataWrapper>() {

    override val checks: List<Check<GroupDataWrapper>> = listOf(
        Check(MISSING_PERMISSION) { checkPermission(it) },
    )

    private fun checkPermission(dataWrapper: GroupDataWrapper): Boolean {
        return dataWrapper.group.ownerId == dataWrapper.authorId
    }
}
