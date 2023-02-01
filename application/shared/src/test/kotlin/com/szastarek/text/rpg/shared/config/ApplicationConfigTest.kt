package com.szastarek.text.rpg.shared.config

import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.assertions.isEqualTo

internal class ApplicationConfigTest : DescribeSpec({

    describe("get application config") {

        it("get properties") {
            // arrange && act && assert
            expectThat(ApplicationConfig) {
                get { environment }.isEqualTo("dev")
            }
        }
    }
})
