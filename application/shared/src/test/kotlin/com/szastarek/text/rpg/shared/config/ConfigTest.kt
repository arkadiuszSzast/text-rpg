package com.szastarek.text.rpg.shared.config

import io.kotest.core.spec.style.DescribeSpec
import io.ktor.server.config.ApplicationConfigurationException
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isNotBlank

class ConfigTest : DescribeSpec({

    describe("config test") {

        it("get property") {
            // arrange && act && assert
            expectThat(getProperty(ConfigKey("application.env")))
                .isNotBlank()
        }

        it("throw exception when property does not exist") {
            // arrange && act && assert
            expectThrows<ApplicationConfigurationException> {
                getProperty(ConfigKey("not_existing"))
            }
        }
    }
})
