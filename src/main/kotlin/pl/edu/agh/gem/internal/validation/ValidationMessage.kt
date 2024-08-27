package pl.edu.agh.gem.internal.validation

object ValidationMessage {
    const val NAME_NOT_BLANK = "Name must not be blank"
    const val NAME_MAX_LENGTH = "Name must not exceed 20 characters"
    const val GROUP_CURRENCY_NOT_EMPTY = "Group Currency must not be empty"
    const val GROUP_CURRENCY_NOT_SUPPORTED = "Group Currency is not supported"
    const val GROUP_CURRENCY_PATTERN = "Group Currency must be a 3-letter uppercase code"

    const val GROUP_BALANCE_NOT_ZERO = "Group balance must be zero"
    const val MISSING_PERMISSION = "Missing permission"
    const val USER_NOT_FOUND = "User not found"
}
