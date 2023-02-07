package com.szastarek.text.rpg.account

import com.szastarek.text.rpg.account.events.AccountCreatedEvent
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.shared.date.CreatedDate
import com.szastarek.text.rpg.shared.password.HashedPassword
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
data class AccountProjection(
    @Contextual override val id: Id<Account>,
    override val emailAddress: EmailAddress,
    val password: HashedPassword,
    val createdAt: CreatedDate,
    val timeZone: TimeZone,
    val lastLoggedInDate: LastLoggedInDate? = null
) : Account {

    companion object {
        fun apply(event: AccountCreatedEvent): AccountProjection {
            return AccountProjection(
                event.accountId,
                event.emailAddress,
                event.hashedPassword,
                event.createdAt,
                event.timeZone
            )
        }
    }
}