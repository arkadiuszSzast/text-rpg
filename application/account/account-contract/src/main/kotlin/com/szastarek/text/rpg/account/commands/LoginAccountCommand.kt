package com.szastarek.text.rpg.account.commands

import com.szastarek.text.rpg.account.LogInFailureError
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.shared.jwt.JwtToken
import com.szastarek.text.rpg.shared.password.RawPassword
import com.trendyol.kediatr.CommandMetadata
import com.trendyol.kediatr.CommandWithResult
import pl.brightinventions.codified.enums.CodifiedEnum

data class LoginAccountCommand(
    val email: EmailAddress,
    val password: RawPassword,
    override val metadata: CommandMetadata? = null
) : CommandWithResult<LoginAccountCommandResult>

sealed class LoginAccountCommandResult

data class LoginAccountCommandSucceed(val email: EmailAddress, val token: JwtToken) : LoginAccountCommandResult()

data class LoginAccountCommandFailure(
    val email: EmailAddress,
    val errorCode: CodifiedEnum<LogInFailureError, String>
) : LoginAccountCommandResult()
