package pl.edu.agh.gem.internal.model

data class NewGroup(
    val id: String,
    val name: String,
    val ownerId: String,
    val members: Set<Member>,
    val currencies: Set<Currency>,
)

fun NewGroup.toGroup(
    attachmentId: String,
    joinCode: String,
) = Group(
    id = id,
    name = name,
    ownerId = ownerId,
    members = members,
    currencies = currencies,
    joinCode = joinCode,
    attachmentId = attachmentId,
)
