package com.szastarek.text.rpg.account.activation.command

import com.szastarek.text.rpg.account.Account
import com.szastarek.text.rpg.account.activation.AccountActivationError
import com.szastarek.text.rpg.shared.jwt.JwtToken
import com.trendyol.kediatr.CommandMetadata
import com.trendyol.kediatr.CommandWithResult
import org.litote.kmongo.Id
import pl.brightinventions.codified.enums.CodifiedEnum

data class ActivateAccountCommand(
    val token: JwtToken,
    override val metadata: CommandMetadata? = null
) : CommandWithResult<ActivateAccountCommandResult>

sealed interface ActivateAccountCommandResult

data class ActivateAccountCommandSucceed(val accountId: Id<Account>) : ActivateAccountCommandResult

data class ActivateAccountCommandFailure(
    val accountId: Id<Account>,
    val errorCode: CodifiedEnum<AccountActivationError, String>
) : ActivateAccountCommandResult
