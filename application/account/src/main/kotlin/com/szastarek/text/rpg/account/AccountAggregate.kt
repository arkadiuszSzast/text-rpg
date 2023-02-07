package com.szastarek.text.rpg.account

import com.szastarek.text.rpg.account.events.AccountCreatedEvent
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.shared.date.CreatedDate
import com.szastarek.text.rpg.shared.password.HashedPassword
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import org.litote.kmongo.Id
import org.litote.kmongo.newId

data class AccountAggregate constructor(
    @SerialName("_id") @Contextual override val id: Id<Account>,
    override val emailAddress: EmailAddress,
    val password: HashedPassword,
    val createdAt: CreatedDate,
    val timeZone: TimeZone,
    val lastLoggedInDate: LastLoggedInDate? = null
) : Account {
    companion object {
        fun create(
            emailAddress: EmailAddress,
            password: HashedPassword,
            timeZoneId: TimeZone,
        ): AccountCreatedEvent {
            return AccountCreatedEvent(
                newId(),
                emailAddress,
                password,
                timeZoneId,
                CreatedDate(Clock.System.now())
            )
        }
    }
}
