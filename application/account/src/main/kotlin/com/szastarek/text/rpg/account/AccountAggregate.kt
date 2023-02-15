package com.szastarek.text.rpg.account

import com.szastarek.acl.authority.AllFeaturesAuthority
import com.szastarek.acl.authority.Authority
import com.szastarek.text.rpg.account.events.AccountCreatedEvent
import com.szastarek.text.rpg.account.events.AccountLoggedInEvent
import com.szastarek.text.rpg.account.events.AccountLoggedInFailureEvent
import com.szastarek.text.rpg.account.events.AccountLoggedInSuccessEvent
import com.szastarek.text.rpg.security.RoleName
import com.szastarek.text.rpg.security.RoleNames
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.shared.date.CreatedDate
import com.szastarek.text.rpg.shared.password.HashedPassword
import com.szastarek.text.rpg.shared.password.RawPassword
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import pl.brightinventions.codified.enums.codifiedEnum

@Serializable
data class AccountAggregate constructor(
    @SerialName("_id") @Contextual override val id: Id<Account>,
    override val emailAddress: EmailAddress,
    val roleName: RoleName,
    val customAuthorities: List<Authority>,
    val password: HashedPassword,
    val createdAt: CreatedDate,
    val timeZone: TimeZone,
    val lastLoggedInDate: LastLoggedInDate? = null
) : Account {
    companion object {
        fun createRegularUser(
            emailAddress: EmailAddress,
            password: HashedPassword,
            timeZoneId: TimeZone,
        ): AccountCreatedEvent {
            return AccountCreatedEvent(
                newId(),
                emailAddress,
                password,
                RoleNames.regularUser,
                emptyList(),
                timeZoneId,
                CreatedDate(Clock.System.now())
            )
        }
    }

    fun logIn(logInRequestPassword: RawPassword): AccountLoggedInEvent {
        if (!password.matches(logInRequestPassword)) {
            return AccountLoggedInFailureEvent(id, LogInFailureError.InvalidPassword.codifiedEnum())
        }

        return AccountLoggedInSuccessEvent(id)
    }
}

