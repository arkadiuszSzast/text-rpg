package com.szastarek.text.rpg.plugins

import arrow.core.NonEmptyList
import com.szastarek.text.rpg.shared.validation.ValidationException
import io.konform.validation.ValidationError
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable
import com.szastarek.text.rpg.shared.validation.ValidationError as InternalValidationError

internal fun Application.configureExceptionsHandling() {
    install(StatusPages) {
        exception<ValidationException> { call, cause ->
            call.respond(
                BadRequest,
                ValidationErrorMessage(cause.validationErrors.toInternalValidationCodes(), cause::class.java.simpleName)
            )
        }
    }
}

internal fun NonEmptyList<ValidationError>.toInternalValidationCodes() =
    this.map { InternalValidationError(it.dataPath, it.message) }.toList()

internal sealed interface HttpError {
    val type: String

    interface HttpValidationErrorMessage : HttpError {
        val validationErrors: List<InternalValidationError>
    }
}

@Serializable
internal data class ValidationErrorMessage(
    override val validationErrors: List<InternalValidationError>,
    override val type: String
) : HttpError.HttpValidationErrorMessage
