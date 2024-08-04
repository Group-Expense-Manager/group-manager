package pl.edu.agh.gem.internal.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.internal.client.AttachmentStoreClient
import pl.edu.agh.gem.internal.client.CurrencyManagerClient
import pl.edu.agh.gem.internal.generator.CodeGenerator
import pl.edu.agh.gem.internal.model.Currency
import pl.edu.agh.gem.internal.model.GroupAttachment
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.persistence.GroupRepository
import pl.edu.agh.gem.util.createGroup
import pl.edu.agh.gem.util.createNewGroup
import pl.edu.agh.gem.validator.ValidatorsException

class GroupServiceTest : ShouldSpec({

    val currencyManagerClient = mock<CurrencyManagerClient>()
    val attachmentStoreClient = mock<AttachmentStoreClient>()
    val groupRepository = mock<GroupRepository>()
    val codeGenerator = mock<CodeGenerator>()
    val groupService = GroupService(
        currencyManagerClient = currencyManagerClient,
        attachmentStoreClient = attachmentStoreClient,
        groupRepository = groupRepository,
        codeGenerator = codeGenerator,
    )

    should("create group successfully") {
        // given
        val newGroup = createNewGroup()
        val group = createGroup()
        val groupAttachment = GroupAttachment(group.attachmentId)
        val currencies = listOf("USD", "EUR", "PLN").map { Currency(it) }
        whenever(currencyManagerClient.getCurrencies()).thenReturn(currencies)
        whenever(groupRepository.insertWithUniqueJoinCode(group)).thenReturn(group)
        whenever(attachmentStoreClient.getGroupInitAttachment(newGroup.id, newGroup.ownerId)).thenReturn(groupAttachment)
        whenever(codeGenerator.generateJoinCode()).thenReturn(group.joinCode)

        // when
        val result = groupService.createGroup(newGroup)

        // then
        verify(groupRepository, times(1)).insertWithUniqueJoinCode(group)
        result shouldBe group
    }

    should("throw ValidatorsException when group creation fails") {
        // given
        val newGroup = createNewGroup()
        val currencies = listOf("USD", "EUR", "CZK").map { Currency(it) }
        whenever(currencyManagerClient.getCurrencies()).thenReturn(currencies)

        // when & then
        shouldThrow<ValidatorsException> {
            groupService.createGroup(newGroup)
        }
        verify(groupRepository, times(0)).insertWithUniqueJoinCode(any())
    }

    should("join group successfully") {
        // given
        val joinCode = "uniqueJoinCode"
        val userId = "userId"
        val group = createGroup(joinCode = joinCode)
        whenever(groupRepository.findByJoinCode(joinCode)).thenReturn(group)
        whenever(groupRepository.save(any())).thenReturn(group.copy(members = listOf(Member(userId))))

        // when
        val result = groupService.joinGroup(joinCode, userId)

        // then
        verify(groupRepository, times(1)).findByJoinCode(joinCode)
        verify(groupRepository, times(1)).save(any())
        result.members.map { it.userId } shouldContain userId
    }

    should("throw MissingGroupException when group with join code does not exist") {
        // given
        val joinCode = "nonExistentJoinCode"
        val userId = "userId"
        whenever(groupRepository.findByJoinCode(joinCode)).thenReturn(null)

        // when & then
        shouldThrow<MissingGroupException> {
            groupService.joinGroup(joinCode, userId)
        }
        verify(groupRepository, times(1)).findByJoinCode(joinCode)
        verify(groupRepository, times(0)).save(any())
    }

    should("throw UserAlreadyInGroupException when user is already in the group") {
        // given
        val joinCode = "uniqueJoinCode"
        val userId = "userId"
        val group = createGroup(joinCode = joinCode, members = listOf(Member(userId)))
        whenever(groupRepository.findByJoinCode(joinCode)).thenReturn(group)

        // when & then
        shouldThrow<UserAlreadyInGroupException> {
            groupService.joinGroup(joinCode, userId)
        }
        verify(groupRepository, times(1)).findByJoinCode(joinCode)
        verify(groupRepository, times(0)).save(any())
    }

    should("get group successfully") {
        // given
        val groupId = "groupId"
        val group = createGroup(id = groupId)
        whenever(groupRepository.findById(groupId)).thenReturn(group)

        // when
        val result = groupService.getGroup(groupId)

        // then
        verify(groupRepository, times(1)).findById(groupId)
        result shouldBe group
    }

    should("throw MissingGroupException when group with id does not exist") {
        // given
        val groupId = "nonExistentGroupId"
        whenever(groupRepository.findById(groupId)).thenReturn(null)

        // when & then
        shouldThrow<MissingGroupException> {
            groupService.getGroup(groupId)
        }
    }

    should("get user groups successfully") {
        // given
        val userId = USER_ID
        val group1 = createGroup()
        val group2 = createGroup()
        whenever(groupRepository.findByUserId(userId)).thenReturn(listOf(group1, group2))

        // when
        val result = groupService.getUserGroups(userId)

        // then
        verify(groupRepository, times(1)).findByUserId(userId)
        result shouldHaveSize 2
        result shouldContain group1
        result shouldContain group2
    }
},)
