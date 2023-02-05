package com.szastarek.text.rpg.event.store

import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.EventDataBuilder
import com.szastarek.text.rpg.shared.json.JsonMapper
import kotlinx.serialization.encodeToString

inline fun <reified T : DomainEvent> T.toEventData(
    eventMetadata: EventMetadata?
): EventData {
    val objectMapper = JsonMapper.defaultMapper
    val valueAsBytes = objectMapper.encodeToString(this).encodeToByteArray()
    val eventMetadataAsBytes = when(eventMetadata) {
        null -> EventMetadata(CorrelationId(eventId), CausationId(eventId))
        else -> eventMetadata
    }.let { objectMapper.encodeToString(it).encodeToByteArray() }

    return EventDataBuilder.json(eventId, eventType.value, valueAsBytes)
        .metadataAsBytes(eventMetadataAsBytes)
        .build()
}
