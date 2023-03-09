package com.szastarek.text.rpg.account.activation.command

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.szastarek.acl.authority.AuthorizedAccountAbilityProvider
import com.szastarek.text.rpg.account.Account
import com.szastarek.text.rpg.account.config.JwtConfig
import com.szastarek.text.rpg.security.generateAccountActivationLinkFeature
import com.szastarek.text.rpg.shared.config.ApplicationConfig
import com.szastarek.text.rpg.shared.jwt.JwtToken
import com.trendyol.kediatr.AsyncCommandWithResultHandler
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import java.util.*
import mu.KotlinLogging
import org.litote.kmongo.Id

internal class GenerateAccountActivationLinkCommandHandler(
    private val appConfig: ApplicationConfig,
    private val jwtConfig: JwtConfig,
    private val acl: AuthorizedAccountAbilityProvider
) : AsyncCommandWithResultHandler<GenerateAccountActivationLinkCommand, GenerateAccountActivationLinkCommandResult> {
    private val logger = KotlinLogging.logger {}

    override suspend fun handleAsync(command: GenerateAccountActivationLinkCommand): GenerateAccountActivationLinkCommandResult {
        acl.ensuring().ensureHasAccessTo(generateAccountActivationLinkFeature)
        val accountId = command.accountId

        logger.debug { "Generating account activation link for account[$accountId]" }

        val token = generateToken(accountId)

        return GenerateAccountActivationLinkCommandResult(buildActivateUrl(token))
    }

    private fun generateToken(accountId: Id<Account>): JwtToken {
        val (secret, issuer, expirationTime) = jwtConfig.activateAccount

        return JWT.create()
            .withIssuer(issuer.value)
            .withSubject(accountId.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + expirationTime.millis))
            .sign(Algorithm.HMAC256(secret.value))
            .let { JwtToken.createOrThrow(it) }
    }

    private fun buildActivateUrl(token: JwtToken): Url {
        val webClientAppUrl = appConfig.webClientAppUrl
        return URLBuilder(webClientAppUrl).appendPathSegments("account", "activate").apply {
            parameters.append("token", token.value)
        }.build()
    }
}
