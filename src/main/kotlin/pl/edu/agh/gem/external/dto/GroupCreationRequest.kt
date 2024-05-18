package pl.edu.agh.gem.external.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.internal.model.Group
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.validation.ValidationMessage.ATTACHMENT_ID_NOT_BLANK
import pl.edu.agh.gem.internal.validation.ValidationMessage.BASE_CURRENCY_NOT_BLANK
import pl.edu.agh.gem.internal.validation.ValidationMessage.BASE_CURRENCY_PATTERN
import pl.edu.agh.gem.internal.validation.ValidationMessage.COLOR_MAX_VALUE
import pl.edu.agh.gem.internal.validation.ValidationMessage.COLOR_MIN_VALUE
import pl.edu.agh.gem.internal.validation.ValidationMessage.NAME_MAX_LENGTH
import pl.edu.agh.gem.internal.validation.ValidationMessage.NAME_NOT_BLANK
import java.util.UUID.randomUUID

data class GroupCreationRequest(
    @field:NotBlank(message = NAME_NOT_BLANK)
    @field:Size(max = 20, message = NAME_MAX_LENGTH)
    val name: String,

    @field:Min(value = 0, message = COLOR_MIN_VALUE)
    @field:Max(value = 16777215, message = COLOR_MAX_VALUE)
    val color: Long,

    val acceptRequired: Boolean,

    @field:NotBlank(message = BASE_CURRENCY_NOT_BLANK)
    @field:Pattern(regexp = "[A-Z]{3}", message = BASE_CURRENCY_PATTERN)
    val baseCurrency: String,

    @field:NotBlank(message = ATTACHMENT_ID_NOT_BLANK)
    val attachmentId: String,
)

fun GroupCreationRequest.toDomain(ownerId: String, joinCode: String) =
    Group(
        id = randomUUID().toString(),
        name = name,
        color = color,
        ownerId = ownerId,
        members = listOf(Member(ownerId)),
        acceptRequired = acceptRequired,
        baseCurrency = listOf(Currency(baseCurrency)),
        joinCode = joinCode,
        attachmentId = attachmentId,
    )
