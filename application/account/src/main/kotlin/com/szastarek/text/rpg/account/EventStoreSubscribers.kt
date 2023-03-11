package com.szastarek.text.rpg.account

import com.szastarek.text.rpg.account.config.MailConfig
import com.szastarek.text.rpg.account.subscriber.accountAggregateUpdater
import com.szastarek.text.rpg.account.subscriber.activationMailSenderSubscriber
import io.ktor.server.application.Application
import org.koin.ktor.ext.get

internal fun Application.configureEventStoreSubscribers() {
    accountAggregateUpdater(get(), get())
    activationMailSenderSubscriber(get(), get(), MailConfig)
}
