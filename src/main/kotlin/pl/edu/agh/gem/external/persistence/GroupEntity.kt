package pl.edu.agh.gem.external.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.internal.model.Group
import pl.edu.agh.gem.internal.model.Member

@Document("groups")
data class GroupEntity(
    @Id
    val id: String,
    val name: String,
    val color: Long,
    val ownerId: String,
    val members: List<MemberEntity>,
    val acceptRequired: Boolean,
    val baseCurrency: List<CurrencyEntity>,
    @Indexed(background = true, unique = true)
    val joinCode: String,
    val attachmentId: String,
)

data class MemberEntity(
    val userId: String,
)

data class CurrencyEntity(
    val code: String,
)

fun Group.toEntity() = GroupEntity(
    id = id,
    name = name,
    color = color,
    ownerId = ownerId,
    members = members.map { it.toEntity() },
    acceptRequired = acceptRequired,
    baseCurrency = groupCurrencies.map { it.toEntity() },
    joinCode = joinCode,
    attachmentId = attachmentId,
)

private fun Member.toEntity() = MemberEntity(
    userId = userId,
)

private fun Currency.toEntity() = CurrencyEntity(
    code = code,
)

fun GroupEntity.toDomain() = Group(
    id = id,
    name = name,
    color = color,
    ownerId = ownerId,
    members = members.map { it.toDomain() },
    acceptRequired = acceptRequired,
    groupCurrencies = baseCurrency.map { it.toDomain() },
    joinCode = joinCode,
    attachmentId = attachmentId,
)

private fun MemberEntity.toDomain() = Member(
    userId = userId,
)

private fun CurrencyEntity.toDomain() = Currency(
    code = code,
)
