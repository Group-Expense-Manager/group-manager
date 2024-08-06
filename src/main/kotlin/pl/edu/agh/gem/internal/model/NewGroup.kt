package pl.edu.agh.gem.internal.model

data class NewGroup(
    val id: String,
    val name: String,
    val ownerId: String,
    val members: Members,
    val acceptRequired: Boolean,
    val groupCurrencies: Currencies,
)

fun NewGroup.toGroup(attachmentId: String, joinCode: String) =
    Group(
        id = id,
        name = name,
        ownerId = ownerId,
        members = members,
        acceptRequired = acceptRequired,
        groupCurrencies = groupCurrencies,
        joinCode = joinCode,
        attachmentId = attachmentId,
    )
