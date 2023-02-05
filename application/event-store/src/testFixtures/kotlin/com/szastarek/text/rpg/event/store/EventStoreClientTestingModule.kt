package com.szastarek.text.rpg.event.store

import com.eventstore.dbclient.Endpoint
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBClientSettings
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.szastarek.event.store.db.EventStoreDB
import com.szastarek.event.store.db.EventStoreDBPlugin
import mu.KotlinLogging
import org.koin.dsl.bind
import org.koin.dsl.module

val eventStoreTestingModule = module {
    single {
        EventStoreDBClient.create(
            EventStoreDBClientSettings.builder()
                .addHost(Endpoint(EventStoreContainer.host, EventStoreContainer.port))
                .buildConnectionSettings()
        )
    }
    single {
        EventStoreDBPersistentSubscriptionsClient.create(
            EventStoreDBClientSettings.builder()
                .addHost(Endpoint(EventStoreContainer.host, EventStoreContainer.port))
                .buildConnectionSettings()
        )
    }
    single {
        EventStoreDBPlugin(
            EventStoreDB.Configuration(
                "esdb://${EventStoreContainer.host}:${EventStoreContainer.port}?tls=false",
                logger = KotlinLogging.logger {}
            )
        )
    } bind EventStoreDB::class
}
