package pl.edu.agh.gem.integration.controler

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import pl.edu.agh.gem.assertion.shouldBody
import pl.edu.agh.gem.assertion.shouldHaveErrors
import pl.edu.agh.gem.assertion.shouldHaveHttpStatus
import pl.edu.agh.gem.external.dto.InternalGroupResponse
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.integration.ability.ServiceTestClient
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.persistence.GroupRepository
import pl.edu.agh.gem.internal.service.MissingGroupException
import pl.edu.agh.gem.util.createGroup

class InternalGroupControllerIT(
    private val service: ServiceTestClient,
    private val groupRepository: GroupRepository,
) : BaseIntegrationSpec({
    should("get group successfully") {
        // given
        val members = listOf(
            Member(userId = "user1"),
            Member(userId = "user2"),
        )
        val group = createGroup(members = members)
        groupRepository.save(group)

        // when
        val response = service.getInternalGroup(group.id)

        // then
        response shouldHaveHttpStatus OK
        response.shouldBody<InternalGroupResponse> {
            members.map { it.userId } shouldContainExactly members.map { it.userId }
            groupCurrencies.map { it.code } shouldContainExactly group.groupCurrencies.map { it.code }
            acceptRequired shouldBe group.acceptRequired
        }
    }

    should("return NOT_FOUND when group does not exist") {
        // given
        val groupId = "nonexistentGroupId"

        // when
        val response = service.getInternalGroup(groupId)

        // then
        response shouldHaveHttpStatus NOT_FOUND
        response shouldHaveErrors {
            errors shouldHaveSize 1
            errors.first().code shouldBe MissingGroupException::class.simpleName
        }
    }
},)
