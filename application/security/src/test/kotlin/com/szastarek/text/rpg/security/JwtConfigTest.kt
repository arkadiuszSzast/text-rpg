package com.szastarek.text.rpg.security

import com.szastarek.text.rpg.security.config.JwtAuthConfig
import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class JwtConfigTest : DescribeSpec({

    describe("get jwt config") {

        it("get properties") {
            // arrange && act && assert
            expectThat(JwtAuthConfig) {
                get { domain }.isEqualTo("https://jwt-test-domain/")
                get { audience }.isEqualTo("jwt-test-audience")
                get { realm }.isEqualTo("jwt-test-realm")
                get { issuer }.isEqualTo("jwt-test-issuer")
                get { secret }.isEqualTo("test-secret")
                get { expirationInMillis }.isEqualTo(10000)
            }
        }
    }
})
