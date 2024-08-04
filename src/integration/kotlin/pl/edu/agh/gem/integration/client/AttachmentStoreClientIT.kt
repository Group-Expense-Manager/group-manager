package pl.edu.agh.gem.integration.client

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_ACCEPTABLE
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.stubInitGroupAttachment
import pl.edu.agh.gem.internal.client.AttachmentStoreClient
import pl.edu.agh.gem.internal.client.AttachmentStoreClientException
import pl.edu.agh.gem.internal.client.CurrencyManagerClientException
import pl.edu.agh.gem.internal.client.RetryableAttachmentStoreClientException
import pl.edu.agh.gem.internal.client.RetryableCurrencyManagerClientException
import pl.edu.agh.gem.util.createGroupAttachmentResponse

class AttachmentStoreClientIT(
    private val attachmentStoreClient: AttachmentStoreClient,
) : BaseIntegrationSpec({
    should("get available currencies") {
        // given
        val attachment = createGroupAttachmentResponse()
        val userId = "userId"
        val groupId = "groupId"
        stubInitGroupAttachment(attachment, userId)

        // when
        val result = attachmentStoreClient.getGroupInitAttachment(groupId, userId)

        // then
        result.id shouldBe attachment.id
    }

    should("throw CurrencyManagerClientException when we send bad request") {
        // given
        val attachment = createGroupAttachmentResponse()
        val userId = "userId"
        val groupId = "groupId"
        stubInitGroupAttachment(attachment, userId, NOT_ACCEPTABLE)

        // when & then
        shouldThrow<AttachmentStoreClientException> {
            attachmentStoreClient.getGroupInitAttachment(groupId, userId)
        }
    }

    should("throw RetryableCurrencyManagerClientException when client has internal error") {
        // given
        val attachment = createGroupAttachmentResponse()
        val userId = "userId"
        val groupId = "groupId"
        stubInitGroupAttachment(attachment, userId, INTERNAL_SERVER_ERROR)

        // when & then
        shouldThrow<RetryableAttachmentStoreClientException> {
            attachmentStoreClient.getGroupInitAttachment(groupId, userId)
        }
    }
},)
