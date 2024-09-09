package pl.edu.agh.gem.external.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.model.NewGroup
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_NOT_EMPTY
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_PATTERN
import pl.edu.agh.gem.internal.validation.ValidationMessage.NAME_MAX_LENGTH
import pl.edu.agh.gem.internal.validation.ValidationMessage.NAME_NOT_BLANK
import java.util.UUID.randomUUID

data class GroupCreationRequest(
    @field:NotBlank(message = NAME_NOT_BLANK)
    @field:Size(max = 20, message = NAME_MAX_LENGTH)
    val name: String,
    @field:NotEmpty(message = GROUP_CURRENCY_NOT_EMPTY)
    @field:Valid
    val groupCurrencies: List<GroupCreationCurrencyDto>,
)

data class GroupCreationCurrencyDto(
    @field:Pattern(regexp = "[A-Z]{3}", message = GROUP_CURRENCY_PATTERN)
    val code: String,
)

fun GroupCreationRequest.toNewGroup(ownerId: String) =
    NewGroup(
        id = randomUUID().toString(),
        name = name,
        ownerId = ownerId,
        members = setOf(Member(ownerId)),
        currencies = groupCurrencies.map { Currency(it.code) }.toSet(),
    )
