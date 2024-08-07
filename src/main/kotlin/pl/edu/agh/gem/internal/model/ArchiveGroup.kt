package pl.edu.agh.gem.internal.model

data class ArchiveGroup(
    val id: String,
    val name: String,
    val ownerId: String,
    val members: List<ArchiveMember>,
    val acceptRequired: Boolean,
    val groupCurrencies: List<ArchiveCurrency>,
    val attachmentId: String,
)

data class ArchiveMember(
    val userId: String,
)

data class ArchiveCurrency(
    val code: String,
)
