package com.szastarek.text.rpg.account.support

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.szastarek.event.store.db.EventStoreDB
import com.szastarek.event.store.db.StreamName
import com.szastarek.text.rpg.HasDatabaseAndTransactionally
import com.szastarek.text.rpg.event.store.HasEventStoreClient
import com.szastarek.text.rpg.event.store.eventStoreTestingModule
import com.szastarek.text.rpg.kmongoTestingModule
import com.szastarek.text.rpg.test.utils.serializationModule
import com.szastarek.text.rpg.transactionally.Transactionally
import io.kotest.common.runBlocking
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import kotlinx.coroutines.delay
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.litote.kmongo.coroutine.CoroutineDatabase

abstract class DatabaseAndEventStoreTest(private val neededModules: List<Module>) : KoinTest,
    HasDatabaseAndTransactionally, HasEventStoreClient, DescribeSpec() {
    constructor(module: Module) : this(listOf(module))

    override val transactionally: Transactionally by inject()
    override val db: CoroutineDatabase by inject()
    override val client: EventStoreDBClient by inject()
    override val persistedSubscriptionClient: EventStoreDBPersistentSubscriptionsClient by inject()
    override val eventStoreDb: EventStoreDB by inject()

    override suspend fun beforeEach(testCase: TestCase) {
        runBlocking {
            db.dropAllCollections()
            val streamsToDelete =
                (eventStoreDb.readAll().events.mapNotNull { it.originalEvent?.streamId } +
                        eventStoreDb.readAll().events.mapNotNull { it.event?.streamId } +
                        eventStoreDb.readAll().events.mapNotNull { it.link?.streamId }).distinct()

            streamsToDelete.forEach { stream ->
                eventStoreDb.deleteStream(StreamName(stream))
            }
        }
    }

    override suspend fun afterSpec(spec: Spec) {
        stopKoin()
    }

    init {
        startKoin {
            modules(
                neededModules.toList().plus(kmongoTestingModule).plus(eventStoreTestingModule).plus(serializationModule)
            )
        }
    }
}

suspend fun CoroutineDatabase.dropAllCollections() = listCollectionNames().forEach { dropCollection(it) }
