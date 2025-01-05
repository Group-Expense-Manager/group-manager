package pl.edu.agh.gem.internal.client

import pl.edu.agh.gem.internal.model.GroupAttachment

interface AttachmentStoreClient {
    fun getGroupInitAttachment(
        groupId: String,
        userId: String,
    ): GroupAttachment
}

class AttachmentStoreClientException(override val message: String?) : RuntimeException()

class RetryableAttachmentStoreClientException(override val message: String?) : RuntimeException()
