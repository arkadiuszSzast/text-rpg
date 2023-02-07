package com.szastarek.text.rpg.account

import arrow.core.Option
import com.szastarek.text.rpg.shared.EmailAddress
import org.litote.kmongo.Id

interface AccountProjectionRepository {
    suspend fun existsByEmail(email: EmailAddress): Boolean
    suspend fun save(accountProjection: AccountProjection): Option<Id<Account>>
}