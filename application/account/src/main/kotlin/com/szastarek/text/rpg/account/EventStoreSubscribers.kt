package com.szastarek.text.rpg.account

import com.szastarek.text.rpg.account.subscribers.accountProjectionUpdater
import io.ktor.server.application.Application
import org.koin.ktor.ext.get

internal fun Application.configureEventStoreSubscribers() {
    accountProjectionUpdater(get(), get())
}
