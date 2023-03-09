package com.szastarek.text.rpg.account.activation.command

import com.szastarek.text.rpg.account.Account
import com.trendyol.kediatr.CommandMetadata
import com.trendyol.kediatr.CommandWithResult
import io.ktor.http.Url
import org.litote.kmongo.Id

data class GenerateAccountActivationLinkCommand(
    val accountId: Id<Account>,
    override val metadata: CommandMetadata? = null,
) : CommandWithResult<GenerateAccountActivationLinkCommandResult>

data class GenerateAccountActivationLinkCommandResult(val url: Url)
