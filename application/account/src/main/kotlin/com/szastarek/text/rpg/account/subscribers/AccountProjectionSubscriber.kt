package com.szastarek.text.rpg.account.subscribers

import com.szastarek.event.store.db.CustomerGroup
import com.szastarek.event.store.db.EventStoreDB
import com.szastarek.text.rpg.account.events.accountEventCategory
import io.ktor.server.application.Application
import kotlinx.coroutines.launch

internal fun Application.accountProjectionUpdater(
    eventStoreDb: EventStoreDB,
    accountProjectionUpdater: AccountProjectionUpdater
) = launch {
    eventStoreDb.subscribePersistentByEventCategory(
        accountEventCategory,
        CustomerGroup("account-projection-updater")
    ) { _, event ->
        accountProjectionUpdater.update(event.event)
    }
}
