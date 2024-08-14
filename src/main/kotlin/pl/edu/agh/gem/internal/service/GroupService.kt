package pl.edu.agh.gem.internal.service

import org.springframework.stereotype.Service
import pl.edu.agh.gem.internal.client.AttachmentStoreClient
import pl.edu.agh.gem.internal.client.CurrencyManagerClient
import pl.edu.agh.gem.internal.client.FinanceAdapterClient
import pl.edu.agh.gem.internal.generator.CodeGenerator
import pl.edu.agh.gem.internal.model.Group
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.model.NewGroup
import pl.edu.agh.gem.internal.model.toGroup
import pl.edu.agh.gem.internal.persistence.ArchiveGroupRepository
import pl.edu.agh.gem.internal.persistence.GroupRepository
import pl.edu.agh.gem.internal.validation.BalanceValidator
import pl.edu.agh.gem.internal.validation.CurrenciesValidator
import pl.edu.agh.gem.internal.validation.GroupDataWrapper
import pl.edu.agh.gem.internal.validation.NewGroupDataWrapper
import pl.edu.agh.gem.internal.validation.PermissionValidator
import pl.edu.agh.gem.validator.ValidatorList.Companion.validatorsOf
import pl.edu.agh.gem.validator.ValidatorsException

@Service
class GroupService(
    private val currencyManagerClient: CurrencyManagerClient,
    private val attachmentStoreClient: AttachmentStoreClient,
    private val groupRepository: GroupRepository,
    private val archiveGroupRepository: ArchiveGroupRepository,
    private val codeGenerator: CodeGenerator,
    private val financeAdapterClient: FinanceAdapterClient,

) {

    private val currenciesValidator = CurrenciesValidator()
    private val balanceValidator = BalanceValidator(financeAdapterClient)
    private val permissionValidator = PermissionValidator()
    private val createGroupValidators = validatorsOf(currenciesValidator)
    private val deleteGroupValidators = validatorsOf(permissionValidator, balanceValidator)

    fun createGroup(newGroup: NewGroup): Group {
        createGroupValidators
            .getFailedValidations(createNewGroupDataWrapper(newGroup))
            .takeIf {
                it.isNotEmpty()
            }
            ?.also { throw ValidatorsException(it) }
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
        deleteGroupValidators
            .getFailedValidations(createGroupDataWrapper(group, authorId))
            .takeIf {
                it.isNotEmpty()
            }
            ?.also { throw DeleteGroupValidationException(it) }
        println("Removing group with id:$groupId")
        groupRepository.remove(group)
        archiveGroupRepository.save(group)
    }

    private fun createNewGroupDataWrapper(newGroup: NewGroup): NewGroupDataWrapper {
        return NewGroupDataWrapper(newGroup, currencyManagerClient.getCurrencies())
    }

    private fun createGroupDataWrapper(group: Group, authorId: String): GroupDataWrapper {
        return GroupDataWrapper(group, authorId)
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
