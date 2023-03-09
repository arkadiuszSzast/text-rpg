package com.szastarek.text.rpg.event.store

import com.eventstore.dbclient.WriteResult
import com.szastarek.event.store.db.EventStoreDB
import com.szastarek.event.store.db.StreamName
import com.trendyol.kediatr.CommandMetadata

suspend inline fun <reified T : DomainEvent> EventStoreDB.appendToStream(event: T) =
    this.appendToStream(event, metadata = null)

suspend inline fun <reified T : DomainEvent> EventStoreDB.appendToStream(event: T, metadata: EventMetadata? = null) =
    this.appendToStream(StreamName(event.streamName), event.toEventData(metadata))

suspend inline fun <reified T : DomainEvent> EventStoreDB.appendToStream(
    event: T,
    commandMetadata: CommandMetadata? = null
): WriteResult {
    val metadata = when (commandMetadata) {
        null -> EventMetadata(CorrelationId(event.eventId), CausationId(event.eventId))
        else -> EventMetadata(
            commandMetadata.correlationId?.let { CorrelationId(it) },
            commandMetadata.causationId?.let { CausationId(it) }
        )
    }
    return this.appendToStream(StreamName(event.streamName), event.toEventData(metadata))
}
