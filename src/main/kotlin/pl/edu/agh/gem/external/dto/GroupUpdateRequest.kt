package pl.edu.agh.gem.external.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.internal.model.GroupUpdate
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_NOT_BLANK
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_NOT_EMPTY
import pl.edu.agh.gem.internal.validation.ValidationMessage.GROUP_CURRENCY_PATTERN
import pl.edu.agh.gem.internal.validation.ValidationMessage.NAME_MAX_LENGTH
import pl.edu.agh.gem.internal.validation.ValidationMessage.NAME_NOT_BLANK

data class GroupUpdateRequest(
    @field:NotBlank(message = NAME_NOT_BLANK)
    @field:Size(max = 20, message = NAME_MAX_LENGTH)
    val name: String,
    val acceptRequired: Boolean,
    @field:NotEmpty(message = GROUP_CURRENCY_NOT_EMPTY)
    val groupCurrencies: List<GroupUpdateCurrencyDto>,
)

data class GroupUpdateCurrencyDto(
    @field:NotBlank(message = GROUP_CURRENCY_NOT_BLANK)
    @field:Pattern(regexp = "[A-Z]{3}", message = GROUP_CURRENCY_PATTERN)
    val code: String,
)

fun GroupUpdateRequest.toGroupUpdate(groupId: String) =
    GroupUpdate(
        id = groupId,
        name = name,
        acceptRequired = acceptRequired,
        currencies = groupCurrencies.map { Currency(it.code) }.toSet(),
    )
