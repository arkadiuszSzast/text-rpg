package com.szastarek.text.rpg.account.subscriber

import arrow.core.Either
import com.szastarek.event.store.db.CustomerGroup
import com.szastarek.event.store.db.EventStoreDB
import com.szastarek.text.rpg.account.event.AccountCreatedEvent
import com.szastarek.text.rpg.event.store.getAs
import com.szastarek.text.rpg.event.store.getMetadata
import io.ktor.server.application.Application
import kotlinx.coroutines.launch

internal fun Application.activationMailSenderSubscriber(
    eventStoreDb: EventStoreDB,
) = launch {
    eventStoreDb.subscribePersistentByEventType(
        AccountCreatedEvent.eventType,
        CustomerGroup("activation-mail-sender")
    ) { _, event ->
        val recordedEvent = event.event
        val eventMetadata = recordedEvent.getMetadata()

        val accountCreatedEvent = recordedEvent.getAs<AccountCreatedEvent>()
        when(accountCreatedEvent) {
            is Either.Right -> {

            }
            is Either.Left -> {

            }
        }
    }
}
