package com.szastarek.text.rpg.account.subscriber

import com.szastarek.event.store.db.CustomerGroup
import com.szastarek.event.store.db.EventStoreDB
import com.szastarek.text.rpg.account.event.accountEventCategory
import io.ktor.server.application.Application
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async

internal fun Application.accountAggregateUpdater(
    eventStoreDb: EventStoreDB,
    accountAggregateUpdater: AccountAggregateUpdater
) = async(SupervisorJob()) {
    eventStoreDb.subscribePersistentByEventCategory(
        accountEventCategory,
        CustomerGroup("account-aggregate-updater")
    ) { _, event ->
        accountAggregateUpdater.update(event.event)
    }
}.start()
