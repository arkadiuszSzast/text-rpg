package com.szastarek.text.rpg.account.event

import com.szastarek.acl.authority.Authority
import com.szastarek.event.store.db.EventCategory
import com.szastarek.event.store.db.EventType
import com.szastarek.text.rpg.account.Account
import com.szastarek.text.rpg.account.AccountStatus
import com.szastarek.text.rpg.event.store.DomainEvent
import com.szastarek.text.rpg.security.RoleName
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.shared.UUIDSerializer
import com.szastarek.text.rpg.shared.date.CreatedDate
import com.szastarek.text.rpg.shared.password.HashedPassword
import java.util.*
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.kmongo.Id
import pl.brightinventions.codified.enums.CodifiedEnum

@Serializable
data class AccountCreatedEvent(
    @Contextual val accountId: Id<Account>,
    val emailAddress: EmailAddress,
    @Serializable(with = AccountStatus.CodifiedSerializer::class)
    val status: CodifiedEnum<AccountStatus, String>,
    val hashedPassword: HashedPassword,
    val roleName: RoleName,
    val customAuthorities: List<Authority>,
    val timeZone: TimeZone,
    val createdAt: CreatedDate,
) : DomainEvent {

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
        val eventType: EventType = EventType("${eventCategory.value}-created")
    }
}
