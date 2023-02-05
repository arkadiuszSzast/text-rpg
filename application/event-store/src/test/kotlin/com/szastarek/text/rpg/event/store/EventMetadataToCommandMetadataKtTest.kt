package com.szastarek.text.rpg.event.store

import com.szastarek.text.rpg.event.store.CausationId
import com.szastarek.text.rpg.event.store.CorrelationId
import com.szastarek.text.rpg.event.store.EventMetadata
import com.szastarek.text.rpg.event.store.toCommandMetadata
import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.UUID

class EventMetadataToCommandMetadataKtTest : DescribeSpec({

    describe("EventMetadataToCommandMetadata") {

        it("can map correctly") {
            // arrange
            val eventMetadata = EventMetadata(CorrelationId(UUID.randomUUID()), CausationId(UUID.randomUUID()))

            // act
            val result = eventMetadata.toCommandMetadata()

            // assert
            expectThat(result) {
                get { correlationId }.isEqualTo(eventMetadata.correlationId?.correlationId)
                get { causationId }.isEqualTo(eventMetadata.causationId?.causationId)
            }
        }

        it("can map correctly when causationId is null") {
            // arrange
            val eventMetadata = EventMetadata(CorrelationId(UUID.randomUUID()), null)

            // act
            val result = eventMetadata.toCommandMetadata()

            // assert
            expectThat(result) {
                get { correlationId }.isEqualTo(eventMetadata.correlationId?.correlationId)
                get { causationId }.isEqualTo(eventMetadata.causationId?.causationId)
            }
        }

        it("can map correctly when correlationId is null") {
            // arrange
            val eventMetadata = EventMetadata(null, CausationId(UUID.randomUUID()))

            // act
            val result = eventMetadata.toCommandMetadata()

            // assert
            expectThat(result) {
                get { correlationId }.isEqualTo(eventMetadata.correlationId?.correlationId)
                get { causationId }.isEqualTo(eventMetadata.causationId?.causationId)
            }
        }

        it("can map correctly when correlationId and causationId are nulls") {
            // arrange
            val eventMetadata = EventMetadata(null, null)

            // act
            val result = eventMetadata.toCommandMetadata()

            // assert
            expectThat(result) {
                get { correlationId }.isEqualTo(eventMetadata.correlationId?.correlationId)
                get { causationId }.isEqualTo(eventMetadata.causationId?.causationId)
            }
        }
    }
})
