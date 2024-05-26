package pl.edu.agh.gem.internal.validation

object ValidationMessage {
    const val NAME_NOT_BLANK = "Name must not be blank"
    const val NAME_MAX_LENGTH = "Name must not exceed 20 characters"
    const val COLOR_MIN_VALUE = "Color value must be greater than or equal to 0"
    const val COLOR_MAX_VALUE = "Color value must be less than or equal to 16777215"
    const val GROUP_CURRENCY_NOT_BLANK = "Group Currency must not be blank"
    const val GROUP_CURRENCY_NOT_EMPTY = "Group Currency must not be empty"
    const val GROUP_CURRENCY_NOT_SUPPORTED = "Group Currency is not supported"
    const val GROUP_CURRENCY_PATTERN = "Group Currency must be a 3-letter uppercase code"
    const val ATTACHMENT_ID_NOT_BLANK = "Attachment ID must not be blank"
}
