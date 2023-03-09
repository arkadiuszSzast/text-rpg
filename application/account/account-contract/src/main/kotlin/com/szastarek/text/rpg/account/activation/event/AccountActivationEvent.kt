package com.szastarek.text.rpg.account.activation.event

import com.szastarek.event.store.db.EventCategory
import com.szastarek.event.store.db.EventType
import com.szastarek.text.rpg.account.Account
import com.szastarek.text.rpg.account.activation.AccountActivationError
import com.szastarek.text.rpg.account.event.accountEventCategory
import com.szastarek.text.rpg.event.store.DomainEvent
import com.szastarek.text.rpg.shared.UUIDSerializer
import java.util.UUID
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.kmongo.Id
import pl.brightinventions.codified.enums.CodifiedEnum

@Serializable
sealed interface AccountActivationEvent : DomainEvent {
    val accountId: Id<Account>
}

@Serializable
data class AccountActivatedEvent(@Contextual override val accountId: Id<Account>) : AccountActivationEvent {

    @Serializable(with = UUIDSerializer::class)
    override val eventId: UUID = UUID.randomUUID()

    @Transient
    override val aggregateId: Id<Account> = accountId
    @Transient
    override val eventCategory: EventCategory = Companion.eventCategory
    @Transient
    override val eventType: EventType = Companion.eventType

    companion object {
        val eventCategory: EventCategory = accountEventCategory
        val eventType: EventType = EventType("${eventCategory.value}-activated")
    }
}

@Serializable
data class AccountActivationFailureEvent(
    @Contextual override val accountId: Id<Account>,
    @Serializable(with = AccountActivationError.CodifiedSerializer::class)
    val reason: CodifiedEnum<AccountActivationError, String>
) : AccountActivationEvent {

    @Serializable(with = UUIDSerializer::class)
    override val eventId: UUID = UUID.randomUUID()

    @Transient
    override val aggregateId: Id<Account> = accountId
    @Transient
    override val eventCategory: EventCategory = Companion.eventCategory
    @Transient
    override val eventType: EventType = Companion.eventType

    companion object {
        val eventCategory: EventCategory = accountEventCategory
        val eventType: EventType = EventType("${eventCategory.value}-activation-failure")
    }

}
