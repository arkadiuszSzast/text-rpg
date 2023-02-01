package com.szastarek.text.rpg.shared

import com.szastarek.text.rpg.shared.validation.mergeAll
import com.szastarek.text.rpg.shared.validation.validate
import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.arrow.isInvalid
import strikt.arrow.isValid
import strikt.assertions.containsExactly
import strikt.assertions.endsWith
import strikt.assertions.isEqualTo
import strikt.assertions.startsWith

class EmailTest : DescribeSpec({

    describe("create email object") {

        it("should be valid") {
            // arrange && act
            val emailAddress = EmailAddress.create("joe@doe.com").validate()

            // assert
            expectThat(emailAddress)
                .isValid()
                .get { value }
                .get { value }
                .isEqualTo("joe@doe.com")
        }

        it("address should be trimmed") {
            // arrange && act
            val emailAddress = EmailAddress.create(" joe@doe.com ")

            // assert
            expectThat(emailAddress)
                .get { value }
                .not().startsWith(" ")
                .not().endsWith(" ")
        }

        describe("validations") {

            it("invalid format") {
                // arrange && act
                val emailAddress = EmailAddress.create("invalid_email").validate()

                // assert
                expectThat(emailAddress)
                    .isInvalid()
                    .get { value.mergeAll().validationErrors.map { it.message } }
                    .containsExactly("validation.invalid_email_format")
            }
        }
    }
})
