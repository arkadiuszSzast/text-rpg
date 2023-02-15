package com.szastarek.text.rpg.account.subscribers

import com.szastarek.event.store.db.CustomerGroup
import com.szastarek.event.store.db.EventStoreDB
import com.szastarek.text.rpg.account.events.accountEventCategory
import io.ktor.server.application.Application
import kotlinx.coroutines.launch

internal fun Application.accountAggregateUpdater(
    eventStoreDb: EventStoreDB,
    accountAggregateUpdater: AccountAggregateUpdater
) = launch {
    eventStoreDb.subscribePersistentByEventCategory(
        accountEventCategory,
        CustomerGroup("account-aggregate-updater")
    ) { _, event ->
        accountAggregateUpdater.update(event.event)
    }
}
