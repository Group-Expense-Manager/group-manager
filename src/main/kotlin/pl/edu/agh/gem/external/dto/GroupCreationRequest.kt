package pl.edu.agh.gem.external.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.model.NewGroup
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_PATTERN
import pl.edu.agh.gem.internal.validation.ValidationMessage.NAME_MAX_LENGTH
import pl.edu.agh.gem.internal.validation.ValidationMessage.NAME_NOT_BLANK
import java.util.UUID.randomUUID

data class GroupCreationRequest(
    @field:NotBlank(message = NAME_NOT_BLANK)
    @field:Size(max = 20, message = NAME_MAX_LENGTH)
    val name: String,
    val acceptRequired: Boolean,
    @field:Pattern(regexp = "[A-Z]{3}", message = GROUP_CURRENCY_PATTERN)
    val groupCurrencies: String,
)

fun GroupCreationRequest.toNewGroup(ownerId: String) =
    NewGroup(
        id = randomUUID().toString(),
        name = name,
        ownerId = ownerId,
        members = setOf(Member(ownerId)),
        acceptRequired = acceptRequired,
        currencies = setOf(Currency(groupCurrencies)),
    )
