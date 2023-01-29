package com.szastarek.text.rpg.config

import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class DatabaseConfigTest : DescribeSpec({

    describe("get database config") {

        it("get properties") {
            // arrange && act && assert
            expectThat(DatabaseConfig) {
                get { name }.isEqualTo("test-database-name")
                get { connectionString }.isEqualTo("mongodb://test-database-connection-string")
            }
        }
    }
})
