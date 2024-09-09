package pl.edu.agh.gem.internal.service

import org.springframework.stereotype.Service
import pl.edu.agh.gem.internal.client.AttachmentStoreClient
import pl.edu.agh.gem.internal.client.CurrencyManagerClient
import pl.edu.agh.gem.internal.client.FinanceAdapterClient
import pl.edu.agh.gem.internal.generator.CodeGenerator
import pl.edu.agh.gem.internal.model.Group
import pl.edu.agh.gem.internal.model.GroupUpdate
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.model.NewGroup
import pl.edu.agh.gem.internal.model.toGroup
import pl.edu.agh.gem.internal.persistence.ArchiveGroupRepository
import pl.edu.agh.gem.internal.persistence.GroupRepository
import pl.edu.agh.gem.internal.validation.BalanceValidator
import pl.edu.agh.gem.internal.validation.CurrenciesValidator
import pl.edu.agh.gem.internal.validation.PermissionValidator
import pl.edu.agh.gem.internal.validation.wrapper.GroupDataWrapper
import pl.edu.agh.gem.internal.validation.wrapper.GroupUpdateDataWrapper
import pl.edu.agh.gem.internal.validation.wrapper.NewGroupDataWrapper
import pl.edu.agh.gem.validator.ValidatorsException
import pl.edu.agh.gem.validator.alsoValidate
import pl.edu.agh.gem.validator.validate

@Service
class GroupService(
    currencyManagerClient: CurrencyManagerClient,
    financeAdapterClient: FinanceAdapterClient,
    private val attachmentStoreClient: AttachmentStoreClient,
    private val groupRepository: GroupRepository,
    private val archiveGroupRepository: ArchiveGroupRepository,
    private val codeGenerator: CodeGenerator,
) {

    private val currenciesValidator = CurrenciesValidator(currencyManagerClient)
    private val balanceValidator = BalanceValidator(financeAdapterClient)
    private val permissionValidator = PermissionValidator()

    fun createGroup(newGroup: NewGroup): Group {
        validateGroupCreation(newGroup)
        val attachment = attachmentStoreClient.getGroupInitAttachment(newGroup.id, newGroup.ownerId)
        val joinCode = codeGenerator.generateJoinCode()
        val group = newGroup.toGroup(attachment.id, joinCode)
        return groupRepository.insertWithUniqueJoinCode(group)
    }

    fun joinGroup(joinCode: String, userId: String): Group {
        val group = groupRepository.findByJoinCode(joinCode) ?: throw MissingGroupException(joinCode)
        group.members.find { it.userId == userId }?.also { throw UserAlreadyInGroupException(group.id, userId) }
        return groupRepository.save(group.withMember(userId))
    }

    fun getGroup(groupId: String): Group {
        return groupRepository.findById(groupId) ?: throw MissingGroupException(groupId)
    }

    fun getUserGroups(userId: String): List<Group> {
        return groupRepository.findByUserId(userId)
    }

    fun removeGroup(groupId: String, authorId: String) {
        val group = getGroup(groupId)
        validateGroupRemove(group, authorId)
        groupRepository.remove(group)
        archiveGroupRepository.save(group)
    }

    fun updateGroup(groupUpdate: GroupUpdate, authorId: String): Group {
        val group = getGroup(groupUpdate.id)
        validateGroupUpdate(group, groupUpdate, authorId)
        return groupRepository.save(
            group.copy(
                currencies = groupUpdate.currencies,
                name = groupUpdate.name,
            ),
        )
    }

    private fun validateGroupCreation(newGroup: NewGroup) {
        val dataWrapper = NewGroupDataWrapper(
            currencies = newGroup.currencies,
        )
        validate(dataWrapper, currenciesValidator)
            .takeIf { it.isNotEmpty() }
            ?.also { throw ValidatorsException(it) }
    }

    private fun validateGroupRemove(group: Group, authorId: String) {
        val dataWrapper = GroupDataWrapper(
            groupId = group.id,
            ownerId = group.ownerId,
            userId = authorId,
            members = group.members,
        )
        validate(dataWrapper, permissionValidator)
            .alsoValidate(dataWrapper, balanceValidator)
            .takeIf { it.isNotEmpty() }
            ?.also { throw DeleteGroupValidationException(it) }
    }

    private fun validateGroupUpdate(group: Group, groupUpdate: GroupUpdate, authorId: String) {
        val dataWrapper = GroupUpdateDataWrapper(
            userId = authorId,
            members = group.members,
            ownerId = group.ownerId,
            currencies = groupUpdate.currencies,
        )
        validate(dataWrapper, currenciesValidator)
            .alsoValidate(dataWrapper, permissionValidator)
            .takeIf { it.isNotEmpty() }
            ?.also { throw ValidatorsException(it) }
    }

    private fun Group.withMember(userId: String): Group {
        return copy(members = members + Member(userId))
    }
}

class MissingGroupException(joinCode: String) :
    RuntimeException("Failed to find group with joinCode:$joinCode")

class UserAlreadyInGroupException(groupId: String, userId: String) :
    RuntimeException("User with id:$userId is already in group with joinCode:$groupId")

class DeleteGroupValidationException(failedValidations: List<String>) : ValidatorsException(failedValidations)
