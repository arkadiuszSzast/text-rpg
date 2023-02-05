package com.szastarek.text.rpg.event.store

import com.szastarek.event.store.db.EventStoreDB
import com.szastarek.event.store.db.StreamName
import com.trendyol.kediatr.CommandMetadata

suspend inline fun <reified T : DomainEvent> EventStoreDB.appendToStream(event: T, metadata: EventMetadata? = null) =
    this.appendToStream(StreamName(event.streamName), event.toEventData(metadata))
