package com.szastarek.text.rpg.shared.password

import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.assertions.contains

class PasswordValidationRulesTest : DescribeSpec({

    describe("default password validation rules") {
        val validator = defaultPasswordValidator

        it("password has at least 12 characters") {
            // arrange && act && assert
            expectThat(validator.validate("12345678901"))
                .get { errors.map { it.message } }
                .contains("validation.password_too_short")

            // arrange && act && assert
            expectThat(validator.validate("123456789012"))
                .get { errors.map { it.message } }
                .not().contains("validation.password_too_short")

            // arrange && act && assert
            expectThat(validator.validate("1234567890123"))
                .get { errors.map { it.message } }
                .not().contains("validation.password_too_short")
        }

        it("password contains number") {
            // arrange && act && assert
            expectThat(validator.validate("ABC"))
                .get { errors.map { it.message } }
                .contains("validation.password_must_contains_number")

            // arrange && act && assert
            expectThat(validator.validate("ABC1"))
                .get { errors.map { it.message } }
                .not().contains("validation.password_must_contains_number")

            // arrange && act && assert
            expectThat(validator.validate("1"))
                .get { errors.map { it.message } }
                .not().contains("validation.password_must_contains_number")
        }

        it("password contains special character") {
            // arrange && act && assert
            expectThat(validator.validate("ABC"))
                .get { errors.map { it.message } }
                .contains("validation.password_must_contains_special_character")

            // arrange && act && assert
            expectThat(validator.validate("ABC#"))
                .get { errors.map { it.message } }
                .not().contains("validation.password_must_contains_special_character")

            // arrange && act && assert
            expectThat(validator.validate("#"))
                .get { errors.map { it.message } }
                .not().contains("validation.password_must_contains_special_character")
        }

        it("password cannot contains whitespace") {
            // arrange && act && assert
            expectThat(validator.validate(" A"))
                .get { errors.map { it.message } }
                .contains("validation.password_cannot_have_whitespaces")

            // arrange && act && assert
            expectThat(validator.validate("AB \tC"))
                .get { errors.map { it.message } }
                .contains("validation.password_cannot_have_whitespaces")

            // arrange && act && assert
            expectThat(validator.validate("ABC "))
                .get { errors.map { it.message } }
                .contains("validation.password_cannot_have_whitespaces")

            // arrange && act && assert
            expectThat(validator.validate("ABC"))
                .get { errors.map { it.message } }
                .not().contains("validation.password_cannot_have_whitespaces")
        }
    }
})
