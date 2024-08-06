package pl.edu.agh.gem.external.dto

import pl.edu.agh.gem.internal.model.GroupAttachment

data class GroupAttachmentResponse(
    val id: String,
)

fun GroupAttachmentResponse.toDomain() =
    GroupAttachment(
        id = id,
    )
