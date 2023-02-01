package com.szastarek.text.rpg.shared.validation

import arrow.core.NonEmptyList
import arrow.core.nel
import arrow.core.nonEmptyListOf
import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo
import strikt.assertions.size

class ValidationExceptionTest : DescribeSpec({

    describe("merging validation exceptions") {

        it("when only single error") {
            // arrange
            val errors = ValidationException(SimpleValidationError(".main", "error_1").nel()).nel()

            // act
            val result = errors.mergeAll()

            // assert
            expectThat(result)
                .get { validationErrors }
                .containsExactly(SimpleValidationError(".main", "error_1"))
        }

        it("when two errors") {
            // arrange
            val errors = nonEmptyListOf(
                ValidationException(SimpleValidationError(".main", "error_1").nel()),
                ValidationException(SimpleValidationError(".main_2", "error_2").nel())
            )

            // act
            val result = errors.mergeAll()

            // assert
            expectThat(result)
                .get { validationErrors }
                .containsExactly(SimpleValidationError(".main", "error_1"), SimpleValidationError(".main_2", "error_2"))
        }

        it("when ten errors") {
            // arrange
            val errors = IntRange(1, 10).map { ValidationException(SimpleValidationError(".main", "error_$it").nel()) }
                .let { NonEmptyList.fromListUnsafe(it) }

            // act
            val result = errors.mergeAll()

            // assert
            expectThat(result)
                .get { validationErrors }
                .and { size.isEqualTo(10) }
                .and { containsExactly(IntRange(1, 10).map { SimpleValidationError(".main", "error_$it") }) }
        }
    }
})

data class SimpleValidationError(override val dataPath: String, override val message: String) :
    io.konform.validation.ValidationError
