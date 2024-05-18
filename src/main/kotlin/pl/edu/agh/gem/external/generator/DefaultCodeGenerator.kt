package pl.edu.agh.gem.external.generator

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import pl.edu.agh.gem.internal.generator.CodeGenerator

@Component
class DefaultCodeGenerator(
    private val generatorProperties: GeneratorProperties,
) : CodeGenerator {
    override fun generateJoinCode(): String {
        val allowedChar = generatorProperties.joinCode.getAllowedCharacters()
        val length = generatorProperties.joinCode.length
        return generateCode(allowedChar, length)
    }

    private fun CodeProperties.getAllowedCharacters() =
        UPPER_CASE_LETTERS.takeIf { this.allowUpperCaseLetter } +
            LOWER_CASE_LETTERS.takeIf { this.allowLowerCaseLetter } +
            DIGITS.takeIf { this.allowDigit } +
            SPECIAL_CHARACTERS.takeIf { this.allowSpecialCharacter }

    private fun generateCode(allowedCharacters: String, length: Long): String {
        return (1..length).map {
            allowedCharacters.random()
        }.joinToString("")
    }

    companion object {
        private const val UPPER_CASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz"
        private const val DIGITS = "0123456789"
        private const val SPECIAL_CHARACTERS = "!$&@"
    }
}

@ConfigurationProperties(prefix = "generator")
data class GeneratorProperties(
    val joinCode: CodeProperties,
)

data class CodeProperties(
    val length: Long,
    val allowUpperCaseLetter: Boolean,
    val allowLowerCaseLetter: Boolean,
    val allowDigit: Boolean,
    val allowSpecialCharacter: Boolean,
)
