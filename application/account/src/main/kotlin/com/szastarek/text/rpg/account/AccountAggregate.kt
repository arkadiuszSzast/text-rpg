package com.szastarek.text.rpg.account

import com.szastarek.acl.authority.Authority
import com.szastarek.text.rpg.account.activation.AccountActivationError
import com.szastarek.text.rpg.account.activation.event.AccountActivatedEvent
import com.szastarek.text.rpg.account.activation.event.AccountActivationEvent
import com.szastarek.text.rpg.account.activation.event.AccountActivationFailureEvent
import com.szastarek.text.rpg.account.event.AccountCreatedEvent
import com.szastarek.text.rpg.account.event.AccountLoggedInEvent
import com.szastarek.text.rpg.account.event.AccountLoggedInFailureEvent
import com.szastarek.text.rpg.account.event.AccountLoggedInSuccessEvent
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
import pl.brightinventions.codified.enums.CodifiedEnum
import pl.brightinventions.codified.enums.codifiedEnum

@Serializable
data class AccountAggregate constructor(
    @SerialName("_id") @Contextual override val id: Id<Account>,
    override val emailAddress: EmailAddress,
    @Serializable(with = AccountStatus.CodifiedSerializer::class)
    val status: CodifiedEnum<AccountStatus, String>,
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
                AccountStatus.Staged.codifiedEnum(),
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
        return when(status) {
            AccountStatus.Staged.codifiedEnum() -> AccountLoggedInFailureEvent(id, LogInFailureError.AccountNotActivated.codifiedEnum())
            AccountStatus.Suspended .codifiedEnum() -> AccountLoggedInFailureEvent(id, LogInFailureError.AccountSuspended.codifiedEnum())
            AccountStatus.Active.codifiedEnum() -> AccountLoggedInSuccessEvent(id)
            else -> AccountLoggedInFailureEvent(id, LogInFailureError.AccountInUnknownStatus.codifiedEnum())
        }
    }

    fun activate(): AccountActivationEvent {
        return when(status.knownOrNull()) {
            AccountStatus.Staged -> AccountActivatedEvent(id)
            AccountStatus.Active -> AccountActivationFailureEvent(id, AccountActivationError.AccountActive.codifiedEnum())
            AccountStatus.Suspended -> AccountActivationFailureEvent(id, AccountActivationError.AccountSuspended.codifiedEnum())
            else -> AccountActivationFailureEvent(id, AccountActivationError.AccountStatusUnknown.codifiedEnum())
        }
    }
}

