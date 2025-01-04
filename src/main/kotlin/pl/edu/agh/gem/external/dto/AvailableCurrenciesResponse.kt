package pl.edu.agh.gem.external.dto

import pl.edu.agh.gem.internal.model.Currency

data class AvailableCurrenciesResponse(
    val currencies: List<CurrencyResponse>,
)

data class CurrencyResponse(
    val code: String,
)

fun AvailableCurrenciesResponse.toDomain() = currencies.map { it.toDomain() }

fun CurrencyResponse.toDomain() =
    Currency(
        code = code,
    )
