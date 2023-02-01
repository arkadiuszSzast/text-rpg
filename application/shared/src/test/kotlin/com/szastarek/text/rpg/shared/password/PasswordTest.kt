package com.szastarek.text.rpg.shared.password

import com.szastarek.text.rpg.shared.validation.mergeAll
import com.szastarek.text.rpg.shared.validation.validate
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import org.mindrot.jbcrypt.BCrypt
import strikt.api.expectThat
import strikt.arrow.isInvalid
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.isEqualTo

class PasswordTest : DescribeSpec({

    describe("create raw password object") {

        it("created object should store raw password") {
            // arrange
            val notHashedPassword = "super_secret"

            // act
            val password = RawPassword(notHashedPassword)

            // assert
            expectThat(password)
                .get { value }
                .isEqualTo(notHashedPassword)
        }

        it("hashing password") {
            // arrange
            val notHashedPassword = "super_secret"

            // act
            val password = RawPassword(notHashedPassword).hashpw()

            // assert
            expectThat(password)
                .get { value }
                .not().isEqualTo(notHashedPassword)
                .and { BCrypt.checkpw(notHashedPassword, this.subject).shouldBeTrue() }
        }
    }

    describe("default validation rules") {
        // arrange && act
        val password = RawPassword("\t").validate()

        // assert
        expectThat(password)
            .isInvalid()
            .get { value.mergeAll().validationErrors.map { it.message } }
            .containsExactlyInAnyOrder(
                ("validation.password_too_short"),
                ("validation.password_must_contains_number"),
                ("validation.password_must_contains_special_character"),
                ("validation.password_cannot_have_whitespaces")
            )
    }
})
