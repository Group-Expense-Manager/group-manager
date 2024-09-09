package pl.edu.agh.gem.internal.model

data class GroupUpdate(
    val id: String,
    val name: String,
    val currencies: Set<Currency>,
)
