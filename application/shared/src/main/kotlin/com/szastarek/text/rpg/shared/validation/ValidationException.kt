package com.szastarek.text.rpg.shared.validation

import arrow.core.NonEmptyList
import arrow.core.nel
import arrow.typeclasses.Semigroup
import com.szastarek.text.rpg.shared.reduce
import io.konform.validation.ValidationError
import io.konform.validation.ValidationErrors
import io.ktor.server.plugins.BadRequestException

data class ValidationException(val validationErrors: NonEmptyList<ValidationError>) :
    BadRequestException(validationErrors.joinToString(",")) {
    constructor(validationError: ValidationError) : this(validationError.nel())

    companion object {
        val semigroup = object : Semigroup<ValidationException> {
            override fun ValidationException.combine(b: ValidationException): ValidationException {
                return ValidationException(this.validationErrors + b.validationErrors)
            }
        }
    }
}

fun NonEmptyList<ValidationException>.mergeAll() =
    this.reduce(ValidationException.semigroup)
