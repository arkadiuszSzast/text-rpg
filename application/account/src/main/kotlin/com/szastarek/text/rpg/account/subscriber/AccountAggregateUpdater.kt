package com.szastarek.text.rpg.account.subscriber

import arrow.core.Either
import com.eventstore.dbclient.RecordedEvent
import com.szastarek.text.rpg.account.AccountAggregate
import com.szastarek.text.rpg.account.AccountAggregateRepository
import com.szastarek.text.rpg.account.activation.event.AccountActivatedEvent
import com.szastarek.text.rpg.account.apply
import com.szastarek.text.rpg.account.event.AccountCreatedEvent
import com.szastarek.text.rpg.event.store.getAs
import com.szastarek.text.rpg.shared.orThrow
import com.szastarek.text.rpg.shared.resourceNotFoundException
import mu.KotlinLogging

internal class AccountAggregateUpdater(private val accountAggregateRepository: AccountAggregateRepository) {
    private val log = KotlinLogging.logger {}

    suspend fun update(event: RecordedEvent) {
        when (event.eventType) {
            AccountCreatedEvent.eventType.value -> applyAccountCreatedEvent(event.getAs())
            AccountActivatedEvent.eventType.value -> applyAccountActivatedEvent(event.getAs())
        }
    }

    private suspend fun applyAccountActivatedEvent(event: Either<Throwable, AccountActivatedEvent>) {
        event
            .map {
                val account = accountAggregateRepository.findById(it.accountId)
                    .orThrow { resourceNotFoundException(it.accountId) }

                accountAggregateRepository.updateById(it.accountId, account.apply(it))
                    .map {
                        log.debug {"Stream group: account-projection-updater applied account-activated event for aggregate $it" }
                    }
                    .tapNone {
                        log.error { "Account with id: ${it.accountId} not found. AccountActivatedEvent won't be applied" }
                    }

            }
            .tapLeft {
                log.error {"Stream group: account-projection-updater failed to apply account-activated event for aggregate. Error: $it" }
            }
    }

    private suspend fun applyAccountCreatedEvent(event: Either<Throwable, AccountCreatedEvent>) {
        when (event) {
            is Either.Right -> {
                accountAggregateRepository.save(AccountAggregate.apply(event.value))
                log.debug {"Stream group: account-projection-updater applied account-created event for aggregate ${event.value.aggregateId}" }
            }

            is Either.Left -> {
                log.error {"Stream group: account-projection-updater failed to apply account-created event for aggregate. Error: ${event.value}" }
                throw event.value
            }
        }
    }

}
