package com.szastarek.text.rpg.account.command

import arrow.core.getOrElse
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.szastarek.event.store.db.EventStoreDB
import com.szastarek.text.rpg.account.Account
import com.szastarek.text.rpg.account.AccountAggregateRepository
import com.szastarek.text.rpg.account.LogInFailureError
import com.szastarek.text.rpg.account.event.AccountLoggedInFailureEvent
import com.szastarek.text.rpg.account.event.AccountLoggedInSuccessEvent
import com.szastarek.text.rpg.event.store.appendToStream
import com.szastarek.text.rpg.security.RoleName
import com.szastarek.text.rpg.security.config.JwtAuthConfig
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.shared.jwt.JwtToken
import com.trendyol.kediatr.AsyncCommandWithResultHandler
import mu.KotlinLogging
import org.litote.kmongo.Id
import pl.brightinventions.codified.enums.codifiedEnum
import java.util.Date

internal class LoginAccountHandler(
    private val jwtConfig: JwtAuthConfig,
    private val accountAggregateRepository: AccountAggregateRepository,
    private val eventStore: EventStoreDB,
) : AsyncCommandWithResultHandler<LoginAccountCommand, LoginAccountCommandResult> {
    private val logger = KotlinLogging.logger {}

    override suspend fun handleAsync(command: LoginAccountCommand): LoginAccountCommandResult {
        val (email, password) = command
        logger.debug { "Starting authentication process for user: [$email]" }

        return accountAggregateRepository.findByEmail(email)
            .map { account->
                when (val event = account.logIn(password)) {
                    is AccountLoggedInFailureEvent -> {
                        logger.warn { "Authenticating $email failed. Error code: ${event.reason}" }
                        eventStore.appendToStream(event, command.metadata)
                        LoginAccountCommandFailure(email, event.reason)
                    }
                    is AccountLoggedInSuccessEvent -> {
                        logger.debug { "Authenticating $email succeeded" }
                        eventStore.appendToStream(event, command.metadata)
                        LoginAccountCommandSucceed(email, getToken(account.id, account.emailAddress, account.roleName))
                    }
                }
            }
            .getOrElse {
                logger.warn { "Authenticating $email failed. No account with given email found" }
                LoginAccountCommandFailure(email, LogInFailureError.AccountNotFound.codifiedEnum())
            }
    }

    private fun getToken(accountId: Id<Account>, email: EmailAddress, role: RoleName) =
        JWT.create()
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.issuer)
            .withSubject(accountId.toString())
            .withClaim("accountId", accountId.toString())
            .withClaim("email", email.value)
            .withClaim("role", role.value)
            .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.expirationInMillis))
            .sign(Algorithm.HMAC256(jwtConfig.secret))
            .let { JwtToken.createOrThrow(it) }
}
