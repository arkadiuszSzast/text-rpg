package com.szastarek.text.rpg.event.store

import com.szastarek.event.store.db.EventCategory
import com.szastarek.event.store.db.EventType
import java.util.UUID
import org.litote.kmongo.Id

interface DomainEvent {

    val eventId: UUID

    val aggregateId: Id<*>

    val eventCategory: EventCategory

    val eventType: EventType
}

inline val DomainEvent.streamName: String
    get() = "${eventCategory.value}-$aggregateId"
