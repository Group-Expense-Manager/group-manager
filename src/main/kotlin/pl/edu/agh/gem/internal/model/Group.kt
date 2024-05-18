package pl.edu.agh.gem.internal.model

data class Group(
    val id: String,
    val name: String,
    val color: Long,
    val ownerId: String,
    val members: Members,
    val acceptRequired: Boolean,
    val baseCurrency: Currencies,
    val joinCode: String,
    val attachmentId: String,
)

typealias Members = List<Member>

data class Member(
    val userId: String,
)

typealias Currencies = List<Currency>

data class Currency(
    val code: String,
)
