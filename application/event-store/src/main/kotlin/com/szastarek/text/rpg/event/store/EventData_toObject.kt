package com.szastarek.text.rpg.event.store

import arrow.core.Either
import com.eventstore.dbclient.RecordedEvent
import com.szastarek.text.rpg.shared.json.JsonMapper
import com.szastarek.text.rpg.shared.json.decodeFromStream

inline fun <reified A> RecordedEvent.getAs(): Either<Throwable, A> {
    val json = JsonMapper.defaultMapper
    return json.decodeFromStream(this.eventData)
}
