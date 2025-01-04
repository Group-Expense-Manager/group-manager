package pl.edu.agh.gem.integration.generator

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.internal.generator.CodeGenerator

class CodeGeneratorIT(
    private val codeGenerator: CodeGenerator,
) : BaseIntegrationSpec({
        should("should generate join code") {
            // given

            // when
            val generatedCode = codeGenerator.generateJoinCode()

            // then
            generatedCode.length shouldBe 6
            generatedCode shouldContain Regex("^[a-zA-Z0-9]+$")
            generatedCode shouldNotContain Regex("[!$&@]")
        }
    })
