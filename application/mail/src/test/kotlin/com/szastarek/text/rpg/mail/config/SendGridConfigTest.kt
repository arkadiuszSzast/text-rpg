package com.szastarek.text.rpg.mail.config

import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class SendGridConfigTest : DescribeSpec({

    describe("get sendgrid config") {

        it("get properties") {
            expectThat(SendGridConfig) {
                get { apiKey }.isEqualTo("sendgrid_api_key")
            }
        }
    }
})
