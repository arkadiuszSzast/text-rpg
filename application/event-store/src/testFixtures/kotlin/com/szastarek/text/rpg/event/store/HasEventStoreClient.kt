package com.szastarek.text.rpg.event.store

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.szastarek.event.store.db.EventStoreDB

interface HasEventStoreClient {
    val client: EventStoreDBClient
    val persistedSubscriptionClient: EventStoreDBPersistentSubscriptionsClient
    val eventStoreDb: EventStoreDB
}
