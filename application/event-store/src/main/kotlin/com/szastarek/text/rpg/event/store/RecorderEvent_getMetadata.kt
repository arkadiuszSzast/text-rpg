package com.szastarek.text.rpg.event.store

import arrow.core.Either
import com.eventstore.dbclient.RecordedEvent
import com.szastarek.text.rpg.shared.json.JsonMapper
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream

@OptIn(ExperimentalSerializationApi::class)
fun RecordedEvent.getMetadata(): Either<Throwable, EventMetadata> {
    val json = JsonMapper.defaultMapper
    return json.decodeFromStream(this.userMetadata.inputStream())
}
