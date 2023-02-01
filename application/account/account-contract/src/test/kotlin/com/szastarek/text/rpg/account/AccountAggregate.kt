package com.szastarek.text.rpg.account

import com.szastarek.acl.AccountContext
import com.szastarek.acl.AccountId
import com.szastarek.text.rpg.HasId
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.shared.date.CreatedDate
import com.szastarek.text.rpg.shared.password.HashedPassword
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import pl.brightinventions.codified.enums.CodifiedEnum

@Serializable
data class AccountAggregate constructor(
    @SerialName("_id") @Contextual override val id: Id<AccountAggregate>,
    val email: EmailAddress,
    val password: HashedPassword,
    val created: CreatedDate,
    @Serializable(with = AccountStatus.CodifiedSerializer::class)
    val status: CodifiedEnum<AccountStatus, String>,
//    @Serializable(with = Role.CodifiedSerializer::class)
//    override var role: CodifiedEnum<Role, String>,
    val timeZoneId: TimeZone,
    val lastLoggedInDate: LastLoggedInDate? = null
) : HasId<AccountAggregate>, AccountContext {

    override val accountId: AccountId
        get() = AccountId(id.toString())

    companion object Events
}

