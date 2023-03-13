package com.szastarek.text.rpg.account.activation.command

import arrow.core.None
import arrow.core.Some
import arrow.core.Validated
import com.auth0.jwt.algorithms.Algorithm
import com.szastarek.event.store.db.EventStoreDB
import com.szastarek.text.rpg.account.Account
import com.szastarek.text.rpg.account.AccountAggregateRepository
import com.szastarek.text.rpg.account.activation.AccountActivationError
import com.szastarek.text.rpg.account.activation.event.AccountActivatedEvent
import com.szastarek.text.rpg.account.activation.event.AccountActivationEvent
import com.szastarek.text.rpg.account.activation.event.AccountActivationFailureEvent
import com.szastarek.text.rpg.account.config.JwtConfig
import com.szastarek.text.rpg.event.store.appendToStream
import com.szastarek.text.rpg.shared.jwt.JwtValidationError
import com.szastarek.text.rpg.shared.jwt.TokenMissingSubjectException
import com.trendyol.kediatr.AsyncCommandWithResultHandler
import com.trendyol.kediatr.CommandMetadata
import mu.KotlinLogging
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId
import pl.brightinventions.codified.enums.codifiedEnum

internal class AccountActivateCommandHandler(
    private val jwtConfig: JwtConfig,
    private val eventStore: EventStoreDB,
    private val accountAggregateRepository: AccountAggregateRepository,
) : AsyncCommandWithResultHandler<ActivateAccountCommand, ActivateAccountCommandResult> {
    private val logger = KotlinLogging.logger {}

    override suspend fun handleAsync(command: ActivateAccountCommand): ActivateAccountCommandResult {
        val (token, metadata) = command
        val accountActivateTokenSecret = jwtConfig.activateAccount.secret
        val accountIdUnsafe = token.getSubjectUnsafe()?.let { ObjectId(it).toId<Account>() }
            ?: throw TokenMissingSubjectException()

        val event =
            when (val tokenValidationResult = token.verify(Algorithm.HMAC256(accountActivateTokenSecret.value))) {
                is Validated.Valid -> {
                    tryActivateAccount(accountIdUnsafe)
                }

                is Validated.Invalid -> {
                    val reason = when (tokenValidationResult.value.knownOrNull()) {
                        JwtValidationError.Expired -> AccountActivationError.TokenExpired
                        else -> AccountActivationError.TokenInvalid
                    }
                    AccountActivationFailureEvent(
                        accountId = accountIdUnsafe,
                        reason = reason.codifiedEnum()
                    )
                }
            }

        return sendEventAndGetResult(event, metadata)
    }

    private suspend fun tryActivateAccount(accountId: Id<Account>): AccountActivationEvent {
        return when (val account = accountAggregateRepository.findById(accountId)) {
            is Some -> account.value.activate()
            is None -> AccountActivationFailureEvent(
                accountId = accountId,
                reason = AccountActivationError.AccountNotFound.codifiedEnum()
            )
        }
    }

    private suspend fun sendEventAndGetResult(
        event: AccountActivationEvent,
        metadata: CommandMetadata?
    ): ActivateAccountCommandResult {
        eventStore.appendToStream(event, metadata)
        return when (event) {
            is AccountActivatedEvent -> {
                logger.debug { "Account[${event.accountId}] activated successfully" }
                ActivateAccountCommandSucceed(event.accountId)
            }

            is AccountActivationFailureEvent -> {
                logger.debug { "Account[${event.accountId}] has not been activated because of ${event.reason}" }
                ActivateAccountCommandFailure(event.accountId, event.reason)
            }
        }
    }
}
