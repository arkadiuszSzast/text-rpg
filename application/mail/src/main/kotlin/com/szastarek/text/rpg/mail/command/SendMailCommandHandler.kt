package com.szastarek.text.rpg.mail.command

import com.szastarek.acl.authority.AuthorizedAccountAbilityProvider
import com.szastarek.event.store.db.EventStoreDB
import com.szastarek.text.rpg.event.store.appendToStream
import com.szastarek.text.rpg.mail.MailAggregate
import com.szastarek.text.rpg.mail.MailSender
import com.szastarek.text.rpg.mail.MailSendingError
import com.szastarek.text.rpg.mail.MailSentResult
import com.szastarek.text.rpg.mail.event.MailSendingErrorEvent
import com.szastarek.text.rpg.mail.event.MailSentSuccessfullyEvent
import com.szastarek.text.rpg.security.onDenied
import com.szastarek.text.rpg.security.sendingMailsFeature
import com.trendyol.kediatr.AsyncCommandWithResultHandler
import com.trendyol.kediatr.CommandMetadata
import mu.KotlinLogging
import org.litote.kmongo.newId

internal class SendMailCommandHandler(
    private val mailSender: MailSender,
    private val eventStore: EventStoreDB,
    private val acl: AuthorizedAccountAbilityProvider
) : AsyncCommandWithResultHandler<SendMailCommand, MailSentResult> {
    private val log = KotlinLogging.logger {}

    override suspend fun handleAsync(command: SendMailCommand): MailSentResult {
        val (subject, from, to, templateId, variables, metadata) = command
        val mail = MailAggregate(newId(), subject, from, to, templateId, variables)
        acl.hasAccessTo(sendingMailsFeature).onDenied {
            handleNoPermissions(mail, metadata)
        }

        return when (val event = mail.send(mailSender)) {
            is MailSentSuccessfullyEvent -> {
                log.debug { "Mail with id: ${event.mailId} sent successfully" }
                eventStore.appendToStream(event, metadata)
                MailSentResult.Success(event.mailId.cast())
            }

            is MailSendingErrorEvent -> {
                log.debug { "Error when sending mail with id: ${event.mailId}. Cause: ${event.error}" }
                eventStore.appendToStream(event, metadata)
                MailSentResult.Error(event.mailId.cast(), event.error)
            }
        }
    }

    private suspend fun handleNoPermissions(
        mail: MailAggregate,
        metadata: CommandMetadata?
    ): MailSentResult.Error {
        val mailSendingErrorEvent = MailSendingErrorEvent(
            mail.id,
            mail.from,
            mail.to,
            mail.subject,
            mail.templateId,
            mail.variables,
            MailSendingError("Sending mail failed because of lack of permissions")
        )
        eventStore.appendToStream(mailSendingErrorEvent, metadata)

        return MailSentResult.Error(mailSendingErrorEvent.mailId.cast(), mailSendingErrorEvent.error)
    }

}
