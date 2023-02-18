package com.szastarek.text.rpg.account.command

import com.szastarek.event.store.db.EventStoreDB
import com.szastarek.text.rpg.account.AccountAggregate
import com.szastarek.text.rpg.account.AccountAggregateRepository
import com.szastarek.text.rpg.event.store.appendToStream
import com.szastarek.text.rpg.shared.validation.ValidationError
import com.szastarek.text.rpg.shared.validation.ValidationException
import com.trendyol.kediatr.AsyncCommandWithResultHandler
import mu.KotlinLogging

internal class CreateAccountCommandHandler(
    private val accountAggregateRepository: AccountAggregateRepository,
    private val eventStore: EventStoreDB,
    ) : AsyncCommandWithResultHandler<CreateAccountCommand, CreateAccountCommandResult> {
    private val logger = KotlinLogging.logger {}

    override suspend fun handleAsync(command: CreateAccountCommand): CreateAccountCommandResult {
        logger.debug { "Starting creating account" }
        val (emailAddress, password, timeZone, metadata) = command
        val isEmailTaken = accountAggregateRepository.existsByEmail(emailAddress)

        if (isEmailTaken) {
            logger.warn { "Email address already taken: $emailAddress" }
            throw ValidationException(
                ValidationError(
                    ".email",
                    "validation.email_already_taken"
                )
            )
        }

        val event = AccountAggregate.createRegularUser(emailAddress, password.hashpw(), timeZone)
        logger.debug { "Account created. Sending event: $event" }
        eventStore.appendToStream(event, metadata)

        return CreateAccountCommandResult(event.accountId)
    }
}
