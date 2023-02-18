package com.szastarek.text.rpg.account.response

import com.szastarek.text.rpg.account.Account
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
data class CreateAccountResponse(@Contextual val accountId: Id<Account>)
