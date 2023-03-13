package com.szastarek.text.rpg.account.subscriber

import com.szastarek.acl.authority.FeatureAccessAuthority
import com.szastarek.acl.withInjectedAuthorities
import com.szastarek.event.store.db.CustomerGroup
import com.szastarek.event.store.db.EventStoreDB
import com.szastarek.text.rpg.account.activation.command.GenerateAccountActivationLinkCommand
import com.szastarek.text.rpg.account.activation.event.AccountActivationMailSentEvent
import com.szastarek.text.rpg.account.activation.mail.ActivateAccountMailVariables
import com.szastarek.text.rpg.account.config.MailConfig
import com.szastarek.text.rpg.account.event.AccountCreatedEvent
import com.szastarek.text.rpg.event.store.appendToStream
import com.szastarek.text.rpg.event.store.getAs
import com.szastarek.text.rpg.event.store.getMetadata
import com.szastarek.text.rpg.event.store.toCommandMetadata
import com.szastarek.text.rpg.mail.MailSentResult
import com.szastarek.text.rpg.mail.command.SendMailCommand
import com.szastarek.text.rpg.security.generateAccountActivationLinkFeature
import com.szastarek.text.rpg.security.sendingMailsFeature
import com.trendyol.kediatr.Mediator
import io.ktor.server.application.Application
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import mu.KotlinLogging

private val neededAuthorities = listOf(
    FeatureAccessAuthority(generateAccountActivationLinkFeature),
    FeatureAccessAuthority(sendingMailsFeature)
)

internal fun Application.activationMailSenderSubscriber(
    eventStoreDb: EventStoreDB,
    mediator: Mediator,
    mailConfig: MailConfig
) = async(SupervisorJob()) {
    val log = KotlinLogging.logger {}

    eventStoreDb.subscribePersistentByEventType(
        AccountCreatedEvent.eventType,
        CustomerGroup("activation-mail-sender")
    ) { _, event ->
        val recordedEvent = event.event
        val eventMetadata = recordedEvent.getMetadata().orNull()

        recordedEvent.getAs<AccountCreatedEvent>()
            .map { accountCreatedEvent ->
                val activateAccountMailConfig = mailConfig.activateAccount
                val sendingResult = withInjectedAuthorities(neededAuthorities) {
                    val activationLink = mediator.send(
                        GenerateAccountActivationLinkCommand(
                            accountCreatedEvent.accountId,
                            eventMetadata?.toCommandMetadata()
                        )
                    ).url
                    val activateAccountMailVariables = ActivateAccountMailVariables(activationLink.toString())
                    mediator.send(
                        SendMailCommand(
                            activateAccountMailConfig.subject,
                            activateAccountMailConfig.sender,
                            accountCreatedEvent.emailAddress,
                            activateAccountMailConfig.templateId,
                            activateAccountMailVariables.toMailVariables(),
                            eventMetadata?.toCommandMetadata()
                        )
                    )
                }

                when (sendingResult) {
                    is MailSentResult.Success -> {
                        val activationMailSentEvent = AccountActivationMailSentEvent(accountCreatedEvent.accountId)
                        eventStoreDb.appendToStream(activationMailSentEvent, eventMetadata?.toCommandMetadata())
                        log.debug { "Activation mail sent to ${accountCreatedEvent.emailAddress}" }
                    }

                    is MailSentResult.Error -> {
                        log.error { "Error when sending activation mail to ${accountCreatedEvent.emailAddress}. Error: ${sendingResult.cause.value}" }
                    }
                }
            }
            .tapLeft {
                log.error { "Failed to deserialize event[${recordedEvent.eventId}] to AccountCreatedEvent. Event data: ${recordedEvent.eventData}" }
            }
    }
}.start()
