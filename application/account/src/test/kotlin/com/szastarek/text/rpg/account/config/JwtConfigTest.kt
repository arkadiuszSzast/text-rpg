package com.szastarek.text.rpg.account.config

import com.szastarek.text.rpg.security.JwtExpirationTime
import com.szastarek.text.rpg.security.JwtIssuer
import com.szastarek.text.rpg.security.JwtProperties
import com.szastarek.text.rpg.security.JwtSecret
import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class JwtConfigTest : DescribeSpec({

    describe("get jwt config test") {

        it("get properties") {
            // arrange && act && assert
            expectThat(JwtConfig) {
                get { activateAccount }.isEqualTo(
                    JwtProperties(
                        JwtSecret("activate_jwt_test_secret"),
                        JwtIssuer("text_rpg_test"),
                        JwtExpirationTime(4320000)
                    )
                )
            }
        }
    }
})
