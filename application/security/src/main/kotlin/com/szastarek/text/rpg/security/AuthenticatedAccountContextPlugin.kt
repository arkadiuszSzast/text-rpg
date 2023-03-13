package com.szastarek.text.rpg.security

import arrow.core.getOrElse
import com.szastarek.acl.AuthenticatedAccountContext
import com.szastarek.text.rpg.account.Account
import com.szastarek.text.rpg.account.query.FindAccountContextByIdQuery
import com.szastarek.text.rpg.shared.exception.resourceNotFoundException
import com.szastarek.text.rpg.shared.koin.getKoinInstance
import com.trendyol.kediatr.Mediator
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.BaseRouteScopedPlugin
import io.ktor.server.application.call
import io.ktor.server.auth.Principal
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelinePhase
import kotlinx.coroutines.withContext
import org.litote.kmongo.id.WrappedObjectId

class AuthenticatedAccountContextPlugin {

    class Configuration

    companion object Feature : BaseRouteScopedPlugin<Configuration, AuthenticatedAccountContextPlugin> {
        override val key = AttributeKey<AuthenticatedAccountContextPlugin>("AuthenticatedAccountContextPlugin")
        private var config = Configuration()
        private val mediator = getKoinInstance<Mediator>()

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: Configuration.() -> Unit
        ): AuthenticatedAccountContextPlugin {
            config = Configuration().apply(configure)
            val feature = AuthenticatedAccountContextPlugin()

            val phase = PipelinePhase("AuthenticatedAccountContextProvider")
            pipeline.insertPhaseAfter(ApplicationCallPipeline.Plugins, phase)

            pipeline.intercept(phase) {
                when (val principal = call.principal<Principal>()) {
                    is JWTPrincipal -> {
                        val accountId = WrappedObjectId<Account>(principal.accountId.value)
                        val accountContext = mediator.send(FindAccountContextByIdQuery(accountId))
                            .getOrElse { throw resourceNotFoundException(accountId) }

                        withContext(coroutineContext + AuthenticatedAccountContext(accountContext)) {
                            proceed()
                        }
                    }

                    else -> proceed()
                }
            }

            return feature
        }
    }
}

