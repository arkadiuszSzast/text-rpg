package com.szastarek.text.rpg.account

import com.szastarek.text.rpg.account.subscribers.accountAggregateUpdater
import io.ktor.server.application.Application
import org.koin.ktor.ext.get

internal fun Application.configureEventStoreSubscribers() {
    accountAggregateUpdater(get(), get())
}
