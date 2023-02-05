package com.szastarek.text.rpg.event.store

import com.szastarek.text.rpg.event.store.config.EventStoreConfig
import com.szastarek.text.rpg.event.store.plugins.configureEventStore
import io.ktor.server.application.Application

@Suppress("unused")
fun Application.eventStoreModule() {
    configureEventStore(EventStoreConfig)
}
