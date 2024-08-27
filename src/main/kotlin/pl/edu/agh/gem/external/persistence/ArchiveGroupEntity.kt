package pl.edu.agh.gem.external.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import pl.edu.agh.gem.internal.model.ArchiveCurrency
import pl.edu.agh.gem.internal.model.ArchiveGroup
import pl.edu.agh.gem.internal.model.ArchiveMember
import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.internal.model.Group
import pl.edu.agh.gem.internal.model.Member

@Document("archive-groups")
data class ArchiveGroupEntity(
    @Id
    val id: String,
    val name: String,
    val ownerId: String,
    val members: List<ArchiveMemberEntity>,
    val acceptRequired: Boolean,
    val groupCurrencies: List<ArchiveCurrencyEntity>,
    val attachmentId: String,
)

data class ArchiveMemberEntity(
    val userId: String,
)

data class ArchiveCurrencyEntity(
    val code: String,
)

fun Group.toArchiveEntity() = ArchiveGroupEntity(
    id = id,
    name = name,
    ownerId = ownerId,
    members = members.map { it.toArchiveEntity() },
    acceptRequired = acceptRequired,
    groupCurrencies = currencies.map { it.toArchiveEntity() },
    attachmentId = attachmentId,
)

private fun Member.toArchiveEntity() = ArchiveMemberEntity(
    userId = userId,
)

private fun Currency.toArchiveEntity() = ArchiveCurrencyEntity(
    code = code,
)

fun ArchiveGroup.toEntity() = ArchiveGroupEntity(
    id = id,
    name = name,
    ownerId = ownerId,
    members = members.map { it.toEntity() },
    acceptRequired = acceptRequired,
    groupCurrencies = groupCurrencies.map { it.toEntity() },
    attachmentId = attachmentId,
)

private fun ArchiveMember.toEntity() = ArchiveMemberEntity(
    userId = userId,
)

private fun ArchiveCurrency.toEntity() = ArchiveCurrencyEntity(
    code = code,
)

fun ArchiveGroupEntity.toDomain() = ArchiveGroup(
    id = id,
    name = name,
    ownerId = ownerId,
    members = members.map { it.toDomain() },
    acceptRequired = acceptRequired,
    groupCurrencies = groupCurrencies.map { it.toDomain() },
    attachmentId = attachmentId,
)

private fun ArchiveMemberEntity.toDomain() = ArchiveMember(
    userId = userId,
)

private fun ArchiveCurrencyEntity.toDomain() = ArchiveCurrency(
    code = code,
)
