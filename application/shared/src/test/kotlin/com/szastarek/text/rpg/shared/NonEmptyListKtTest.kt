package com.szastarek.text.rpg.shared

import arrow.core.nel
import arrow.core.nonEmptyListOf
import com.szastarek.text.rpg.shared.validation.SimpleValidationError
import com.szastarek.text.rpg.shared.validation.ValidationException
import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.assertions.containsExactly

class NonEmptyListKtTest : DescribeSpec({

    describe("reduce non empty list") {

        it("can reduce list with single element") {
            // arrange
            val errors = ValidationException(nonEmptyListOf(SimpleValidationError(".", "error_1"))).nel()

            // act
            val result = errors.reduce(ValidationException.semigroup)

            // assert
            expectThat(result).get { validationErrors }.containsExactly(SimpleValidationError(".", "error_1"))
        }

        it("can reduce list with two elements") {
            // arrange
            val errors = nonEmptyListOf(
                ValidationException(nonEmptyListOf(SimpleValidationError(".", "error_1"))),
                ValidationException(nonEmptyListOf(SimpleValidationError(".", "error_2")))
            )

            // act
            val result = errors.reduce(ValidationException.semigroup)

            // assert
            expectThat(result).get { validationErrors }
                .containsExactly(
                    SimpleValidationError(".", "error_1"),
                    SimpleValidationError(".", "error_2")
                )
        }

        it("can reduce list with two elements and with different size") {
            // arrange
            val errors = nonEmptyListOf(
                ValidationException(nonEmptyListOf(SimpleValidationError(".", "error_1"))),
                ValidationException(
                    nonEmptyListOf(
                        SimpleValidationError(".", "error_2"),
                        SimpleValidationError(".", "error_3")
                    )
                )
            )

            // act
            val result = errors.reduce(ValidationException.semigroup)

            // assert
            expectThat(result).get { validationErrors }.containsExactly(
                SimpleValidationError(".", "error_1"),
                SimpleValidationError(".", "error_2"),
                SimpleValidationError(".", "error_3")
            )
        }
    }
})