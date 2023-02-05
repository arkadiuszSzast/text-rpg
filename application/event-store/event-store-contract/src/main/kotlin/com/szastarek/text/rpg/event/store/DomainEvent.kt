package com.szastarek.text.rpg.event.store

import org.litote.kmongo.Id
import java.util.UUID

interface DomainEvent {

    val eventId: UUID

    val aggregateId: Id<*>

    val eventCategory: EventCategory

    val eventType: EventType
}

inline val DomainEvent.streamName: String
    get() = "${eventCategory.value}-$aggregateId"
