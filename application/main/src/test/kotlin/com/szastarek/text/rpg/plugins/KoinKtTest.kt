package com.szastarek.text.rpg.plugins

import io.kotest.core.spec.style.DescribeSpec
import io.ktor.server.testing.testApplication
import org.koin.ktor.ext.getKoin
import org.koin.test.check.checkModules

class KoinKtTest : DescribeSpec({

    describe("Koin feature install") {

        describe("check installed modules") {

            it("verify all modules") {
                testApplication {
                    this.application {
                        getKoin().checkModules()
                    }
                }
            }
        }
    }
})
