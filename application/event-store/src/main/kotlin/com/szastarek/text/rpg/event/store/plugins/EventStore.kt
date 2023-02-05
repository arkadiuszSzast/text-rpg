package com.szastarek.text.rpg.event.store.plugins

import com.szastarek.event.store.db.EventStoreDB
import com.szastarek.event.store.db.eventStoreDb
import com.szastarek.text.rpg.event.store.config.EventStoreConfig
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module

internal fun Application.configureEventStore(eventStoreConfig: EventStoreConfig) {
    install(EventStoreDB) {
        connectionString = eventStoreConfig.connectionString
    }
}

val Application.eventStoreDbKoinModule
    get() = module { single { eventStoreDb } }
