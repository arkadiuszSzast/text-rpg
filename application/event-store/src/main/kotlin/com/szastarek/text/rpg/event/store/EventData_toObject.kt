package com.szastarek.text.rpg.event.store

import arrow.core.Either
import com.eventstore.dbclient.RecordedEvent
import com.szastarek.text.rpg.shared.json.JsonMapper
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified A> RecordedEvent.getAs(): Either<Throwable, A> {
    val json = JsonMapper.defaultMapper
    return json.decodeFromStream(this.eventData.inputStream())
}
