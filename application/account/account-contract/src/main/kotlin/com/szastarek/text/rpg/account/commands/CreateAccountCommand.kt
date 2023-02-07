package com.szastarek.text.rpg.account.commands

import com.szastarek.text.rpg.account.Account
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.shared.password.RawPassword
import com.trendyol.kediatr.CommandMetadata
import com.trendyol.kediatr.CommandWithResult
import kotlinx.datetime.TimeZone
import org.litote.kmongo.Id

data class CreateAccountCommand(
    val emailAddress: EmailAddress,
    val password: RawPassword,
    val timeZone: TimeZone,
    override val metadata: CommandMetadata? = null
) : CommandWithResult<CreateAccountCommandResult>

data class CreateAccountCommandResult(val accountId: Id<Account>)
