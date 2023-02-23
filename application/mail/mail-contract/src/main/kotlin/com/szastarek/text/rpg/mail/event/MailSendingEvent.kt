package com.szastarek.text.rpg.mail.event

import com.szastarek.event.store.db.EventCategory
import com.szastarek.event.store.db.EventType
import com.szastarek.text.rpg.event.store.DomainEvent
import com.szastarek.text.rpg.mail.Mail
import com.szastarek.text.rpg.mail.MailSendingError
import com.szastarek.text.rpg.mail.MailSubject
import com.szastarek.text.rpg.mail.MailTemplateId
import com.szastarek.text.rpg.mail.MailVariables
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.shared.UUIDSerializer
import java.util.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.kmongo.Id

sealed class MailSendingEvent

@Serializable
data class MailSentSuccessfullyEvent(
    @Contextual val mailId: Id<Mail>,
    val from: EmailAddress,
    val to: EmailAddress,
    val subject: MailSubject,
    val templateId: MailTemplateId,
    val variables: MailVariables
) : DomainEvent, MailSendingEvent() {

    @Serializable(with = UUIDSerializer::class)
    override val eventId: UUID = UUID.randomUUID()

    @Transient
    override val aggregateId: Id<Mail> = mailId
    @Transient
    override val eventCategory: EventCategory = Companion.eventCategory
    @Transient
    override val eventType: EventType = Companion.eventType
    companion object {
        val eventCategory = mailEventCategory
        val eventType = EventType("${eventCategory.value}-sent-successfully")
    }
}

@Serializable
data class MailSendingErrorEvent(
    @Contextual val mailId: Id<Mail>,
    val from: EmailAddress,
    val to: EmailAddress,
    val subject: MailSubject,
    val templateId: MailTemplateId,
    val variables: MailVariables,
    val error: MailSendingError
) : DomainEvent, MailSendingEvent() {

    @Serializable(with = UUIDSerializer::class)
    override val eventId: UUID = UUID.randomUUID()

    @Transient
    override val aggregateId: Id<Mail> = mailId
    @Transient
    override val eventCategory: EventCategory = Companion.eventCategory
    @Transient
    override val eventType: EventType = Companion.eventType

    companion object {
        val eventCategory = mailEventCategory
        val eventType = EventType("${eventCategory.value}-sending-error")
    }
}

