package com.szastarek.text.rpg

import com.trendyol.kediatr.Command
import com.trendyol.kediatr.CommandMetadata
import com.trendyol.kediatr.CommandWithResult
import com.trendyol.kediatr.Notification
import com.trendyol.kediatr.Query
import io.kotest.core.spec.style.DescribeSpec
import pl.brightinventions.codified.enums.codifiedEnum
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class KediatrRequestTypeExtractorTest : DescribeSpec({

    describe("KediatrRequestTypeExtractor") {

        it("can extract command type") {
            // arrange && act
            val result = KediatrRequestTypeExtractor.extract(SimpleCommand())

            // assert
            expectThat(result).isEqualTo(KediatrRequestType.Command.codifiedEnum())
        }

        it("can extract commandWithResult type") {
            // arrange && act
            val result = KediatrRequestTypeExtractor.extract(SimpleCommandWithResult())

            // assert
            expectThat(result).isEqualTo(KediatrRequestType.CommandWithResult.codifiedEnum())
        }

        it("can extract query type") {
            // arrange && act
            val result = KediatrRequestTypeExtractor.extract(SimpleQuery())

            // assert
            expectThat(result).isEqualTo(KediatrRequestType.Query.codifiedEnum())
        }

        it("can extract notification type") {
            // arrange && act
            val result = KediatrRequestTypeExtractor.extract(SimpleNotification())

            // assert
            expectThat(result).isEqualTo(KediatrRequestType.Notification.codifiedEnum())
        }

        it("should return unknown when not found") {
            // arrange && act
            val result = KediatrRequestTypeExtractor.extract(Dog())

            // assert
            expectThat(result) {
                get { knownOrNull() }.isNull()
                get { code() }.isEqualTo("unknown")
            }
        }
    }
})

private class SimpleCommand(override val metadata: CommandMetadata? = null) : Command
private class SimpleCommandWithResult(override val metadata: CommandMetadata? = null) : CommandWithResult<String>
private class SimpleQuery : Query<String>
private class SimpleNotification : Notification
private class Dog
