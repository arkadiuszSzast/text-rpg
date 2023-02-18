package com.szastarek.text.rpg.account.event

import com.szastarek.event.store.db.EventCategory
import com.szastarek.event.store.db.EventType
import com.szastarek.text.rpg.account.Account
import com.szastarek.text.rpg.account.LogInFailureError
import com.szastarek.text.rpg.event.store.DomainEvent
import com.szastarek.text.rpg.shared.UUIDSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import pl.brightinventions.codified.enums.CodifiedEnum
import java.util.UUID
import kotlinx.serialization.Transient

@Serializable
sealed class AccountLoggedInEvent : DomainEvent

@Serializable
data class AccountLoggedInSuccessEvent(
    @Contextual val accountId: Id<Account>,
    val attemptDate: Instant = Clock.System.now()
) : AccountLoggedInEvent() {
    @Serializable(with = UUIDSerializer::class)
    override val eventId: UUID = UUID.randomUUID()

    @Contextual
    override val aggregateId = accountId

    @Transient
    override val eventCategory = Companion.eventCategory

    @Transient
    override val eventType = Companion.eventType

    companion object {
        val eventCategory: EventCategory = accountEventCategory
        val eventType: EventType = EventType("${eventCategory.value}-logged-in-successfully")
    }
}

@Serializable
data class AccountLoggedInFailureEvent(
    @Contextual val accountId: Id<Account>,
    @Serializable(with = LogInFailureError.CodifiedSerializer::class)
    val error: CodifiedEnum<LogInFailureError, String>,
    val attemptDate: Instant = Clock.System.now()
) : AccountLoggedInEvent() {
    @Serializable(with = UUIDSerializer::class)
    override val eventId: UUID = UUID.randomUUID()

    @Contextual
    override val aggregateId = accountId

    @Transient
    override val eventCategory = Companion.eventCategory

    @Transient
    override val eventType = Companion.eventType

    companion object {
        val eventCategory: EventCategory = accountEventCategory
        val eventType: EventType = EventType("${eventCategory.value}-login-failure")
    }
}
