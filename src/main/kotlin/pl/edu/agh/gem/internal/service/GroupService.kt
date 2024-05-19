package pl.edu.agh.gem.internal.service

import org.springframework.stereotype.Service
import pl.edu.agh.gem.internal.client.CurrencyManagerClient
import pl.edu.agh.gem.internal.model.Group
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

    private fun createGroupDataWrapper(group: Group): GroupDataWrapper {
        return GroupDataWrapper(group, currencyManagerClient.getCurrencies())
    }
}
