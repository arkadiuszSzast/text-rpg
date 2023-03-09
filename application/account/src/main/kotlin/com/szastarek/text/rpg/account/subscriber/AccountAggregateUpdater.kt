package com.szastarek.text.rpg.account.subscriber

import arrow.core.Either
import com.eventstore.dbclient.RecordedEvent
import com.szastarek.text.rpg.account.AccountAggregate
import com.szastarek.text.rpg.account.AccountAggregateRepository
import com.szastarek.text.rpg.account.apply
import com.szastarek.text.rpg.account.event.AccountCreatedEvent
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
        when(event) {
            is Either.Right -> {
                accountAggregateRepository.save(AccountAggregate.apply(event.value))
                log.debug("Stream group: account-projection-updater applied account-created event for aggregate ${event.value.aggregateId}")
            }
            is Either.Left -> {
                log.error("Stream group: account-projection-updater failed to apply account-created event for aggregate. Error: ${event.value}")
                throw event.value
            }
        }
    }

}
