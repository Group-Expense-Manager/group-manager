package pl.edu.agh.gem.external.controller

import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import pl.edu.agh.gem.error.SimpleError
import pl.edu.agh.gem.error.SimpleErrorsHolder
import pl.edu.agh.gem.error.handleError
import pl.edu.agh.gem.error.withCode
import pl.edu.agh.gem.error.withDetails
import pl.edu.agh.gem.error.withMessage
import pl.edu.agh.gem.error.withUserMessage
import pl.edu.agh.gem.exception.UserWithoutGroupAccessException
import pl.edu.agh.gem.internal.client.AttachmentStoreClientException
import pl.edu.agh.gem.internal.client.CurrencyManagerClientException
import pl.edu.agh.gem.internal.client.RetryableAttachmentStoreClientException
import pl.edu.agh.gem.internal.client.RetryableCurrencyManagerClientException
import pl.edu.agh.gem.internal.service.DeleteGroupValidationException
import pl.edu.agh.gem.internal.service.MissingGroupException
import pl.edu.agh.gem.internal.service.UserAlreadyInGroupException
import pl.edu.agh.gem.validator.ValidatorsException

@ControllerAdvice
@Order(LOWEST_PRECEDENCE)
class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        exception: MethodArgumentNotValidException,
    ): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleNotValidException(exception), BAD_REQUEST)
    }

    @ExceptionHandler(ValidatorsException::class)
    fun handleValidatorsException(exception: ValidatorsException): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleValidatorException(exception), BAD_REQUEST)
    }

    @ExceptionHandler(DeleteGroupValidationException::class)
    fun handleDeleteGroupValidationException(exception: DeleteGroupValidationException): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleValidatorException(exception), UNPROCESSABLE_ENTITY)
    }

    @ExceptionHandler(MissingGroupException::class)
    fun handleMissingGroupException(exception: MissingGroupException): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleError(exception), NOT_FOUND)
    }

    @ExceptionHandler(UserAlreadyInGroupException::class)
    fun handleUserAlreadyInGroupException(exception: UserAlreadyInGroupException): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleError(exception), CONFLICT)
    }

    @ExceptionHandler(UserWithoutGroupAccessException::class)
    fun handleUserWithoutGroupAccessException(
        exception: UserWithoutGroupAccessException,
    ): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleError(exception), FORBIDDEN)
    }

    @ExceptionHandler(CurrencyManagerClientException::class)
    fun handleCurrencyManagerClientException(
        exception: CurrencyManagerClientException,
    ): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleError(exception), INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(RetryableCurrencyManagerClientException::class)
    fun handleRetryableCurrencyManagerClientException(
        exception: RetryableCurrencyManagerClientException,
    ): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleError(exception), INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(AttachmentStoreClientException::class)
    fun handleAttachmentStoreClientException(
        exception: AttachmentStoreClientException,
    ): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleError(exception), INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(RetryableAttachmentStoreClientException::class)
    fun handleRetryableAttachmentStoreClientException(
        exception: RetryableAttachmentStoreClientException,
    ): ResponseEntity<SimpleErrorsHolder> {
        return ResponseEntity(handleError(exception), INTERNAL_SERVER_ERROR)
    }

    private fun handleValidatorException(exception: ValidatorsException): SimpleErrorsHolder {
        val errors = exception.failedValidations
            .map { error ->
                SimpleError()
                    .withCode("VALIDATOR_ERROR")
                    .withDetails(error)
                    .withMessage(error)
            }
        return SimpleErrorsHolder(errors)
    }

    private fun handleNotValidException(exception: MethodArgumentNotValidException): SimpleErrorsHolder {
        val errors = exception.bindingResult.fieldErrors
            .map { error ->
                SimpleError()
                    .withCode("VALIDATION_ERROR")
                    .withDetails(error.field)
                    .withUserMessage(error.defaultMessage)
                    .withMessage(error.defaultMessage)
            }
        return SimpleErrorsHolder(errors)
    }
}
