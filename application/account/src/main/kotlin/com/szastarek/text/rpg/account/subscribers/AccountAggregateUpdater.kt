package com.szastarek.text.rpg.account.subscribers

import arrow.core.Either
import com.eventstore.dbclient.RecordedEvent
import com.szastarek.text.rpg.account.AccountAggregate
import com.szastarek.text.rpg.account.AccountAggregateRepository
import com.szastarek.text.rpg.account.apply
import com.szastarek.text.rpg.account.events.AccountCreatedEvent
import com.szastarek.text.rpg.event.store.getAs
import mu.KotlinLogging

internal class AccountAggregateUpdater(private val accountAggregateRepository: AccountAggregateRepository) {
    private val log = KotlinLogging.logger {}

    suspend fun update(event: RecordedEvent) {
        when (event.eventType) {
            AccountCreatedEvent.eventType.value -> applyAccountCreatedEvent(event.getAs())
        }
    }

    private suspend fun applyAccountCreatedEvent(event: Either<Throwable, AccountCreatedEvent>) {
        event.map {
            accountAggregateRepository.save(AccountAggregate.apply(it))
                .tap { log.debug("Stream group: account-projection-updater applied account-created event for aggregate $it") }
        }.tapLeft {
            log.error("Stream group: account-projection-updater failed to apply account-created event for aggregate $it")
            throw it
        }

    }

}
