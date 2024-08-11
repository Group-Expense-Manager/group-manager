package pl.edu.agh.gem.internal.validation.wrapper

import pl.edu.agh.gem.validator.DataWrapper

interface GroupIdentifierDataWrapper : DataWrapper {
    val groupId: String
}
