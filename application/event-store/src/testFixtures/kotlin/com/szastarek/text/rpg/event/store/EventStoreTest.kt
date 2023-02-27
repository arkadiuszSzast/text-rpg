package com.szastarek.text.rpg.event.store

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.szastarek.event.store.db.EventStoreDB
import com.szastarek.event.store.db.StreamName
import com.szastarek.text.rpg.test.utils.serializationModule
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.test.KoinTest
import org.koin.test.inject

abstract class EventStoreTest(vararg neededModules: Module) : KoinTest, HasEventStoreClient, DescribeSpec() {

    override val client: EventStoreDBClient by inject()
    override val persistedSubscriptionClient: EventStoreDBPersistentSubscriptionsClient by inject()
    override val eventStoreDb: EventStoreDB by inject()

    override suspend fun beforeEach(testCase: TestCase) {
        val streamsToDelete =
            (eventStoreDb.readAll().events.mapNotNull { it.originalEvent?.streamId } +
                    eventStoreDb.readAll().events.mapNotNull { it.event?.streamId } +
                    eventStoreDb.readAll().events.mapNotNull { it.link?.streamId }).distinct()

        streamsToDelete.forEach { stream ->
            eventStoreDb.deleteStream(StreamName(stream))
        }
    }

    override suspend fun afterSpec(spec: Spec) {
        stopKoin()
    }

    init {
        startKoin { modules(neededModules.toList().plus(eventStoreTestingModule).plus(serializationModule)) }
    }
}
