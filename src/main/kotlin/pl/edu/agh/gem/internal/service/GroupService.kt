package pl.edu.agh.gem.internal.service

import org.springframework.stereotype.Service
import pl.edu.agh.gem.internal.client.CurrencyManagerClient
import pl.edu.agh.gem.internal.model.Group
import pl.edu.agh.gem.internal.model.Member
import pl.edu.agh.gem.internal.persistence.GroupRepository
import pl.edu.agh.gem.internal.validation.CurrenciesValidator
import pl.edu.agh.gem.internal.validation.GroupDataWrapper
import pl.edu.agh.gem.validator.ValidatiorList.Companion.validatorsOf
import pl.edu.agh.gem.validator.ValidatorsException

@Service
class GroupService(
    private val currencyManagerClient: CurrencyManagerClient,
    private val groupRepository: GroupRepository,
) {

    private val currenciesValidator = CurrenciesValidator()
    private val createGroupValidators = validatorsOf(currenciesValidator)

    fun createGroup(group: Group): Group {
        createGroupValidators
            .getFailedValidations(createGroupDataWrapper(group))
            .takeIf {
                it.isNotEmpty()
            }
            ?.also { throw ValidatorsException(it) }
        return groupRepository.insertWithUniqueJoinCode(group)
    }

    fun joinGroup(joinCode: String, userId: String): Group {
        val group = groupRepository.findByJoinCode(joinCode) ?: throw MissingGroupException(joinCode)
        group.members.find { it.userId == userId }?.also { throw UserAlreadyInGroupException(group.id, userId) }
        return groupRepository.save(group.withMember(userId))
    }

    private fun createGroupDataWrapper(group: Group): GroupDataWrapper {
        return GroupDataWrapper(group, currencyManagerClient.getCurrencies())
    }

    private fun Group.withMember(userId: String): Group {
        return copy(members = members + Member(userId))
    }
}

class MissingGroupException(joinCode: String) :
    RuntimeException("Failed to find group with joinCode:$joinCode")

class UserAlreadyInGroupException(groupId: String, userId: String) :
    RuntimeException("User with id:$userId is already in group with joinCode:$groupId")
