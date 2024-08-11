package pl.edu.agh.gem.internal.model

data class Group(
    val id: String,
    val name: String,
    val ownerId: String,
    val members: Set<Member>,
    val acceptRequired: Boolean,
    val currencies: Set<Currency>,
    val joinCode: String,
    val attachmentId: String,
)

data class Member(
    val userId: String,
)

data class Currency(
    val code: String,
)
