package com.szastarek.text.rpg.mail

import com.szastarek.text.rpg.mail.event.MailSendingErrorEvent
import com.szastarek.text.rpg.mail.event.MailSendingEvent
import com.szastarek.text.rpg.mail.event.MailSentSuccessfullyEvent
import com.szastarek.text.rpg.shared.EmailAddress
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
data class MailAggregate(
    @Contextual override val id: Id<Mail>,
    val subject: MailSubject,
    val from: EmailAddress,
    val to: EmailAddress,
    val templateId: MailTemplateId,
    val variables: MailVariables
) : Mail {
    suspend fun send(mailSender: MailSender): MailSendingEvent =
        when (val result = mailSender.send(this)) {
            is MailSentResult.Success -> MailSentSuccessfullyEvent(id, from, to, subject, templateId, variables)
            is MailSentResult.Error -> MailSendingErrorEvent(id, from, to, subject, templateId, variables, result.cause)
        }
}
