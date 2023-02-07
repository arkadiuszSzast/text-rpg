package com.szastarek.text.rpg.account.adapter

import arrow.core.Option
import arrow.core.toOption
import com.szastarek.text.rpg.account.Account
import com.szastarek.text.rpg.account.AccountProjectionRepository
import com.szastarek.text.rpg.account.AccountProjection
import com.szastarek.text.rpg.shared.EmailAddress
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.eq
import org.litote.kmongo.id.WrappedObjectId
import org.litote.kmongo.id.toId

internal class AccountProjectionMongoRepository(
    private val collection: CoroutineCollection<AccountProjection>
) : AccountProjectionRepository {
    override suspend fun existsByEmail(email: EmailAddress): Boolean {
        return collection.findOne(AccountProjection::emailAddress eq email).toOption().isDefined()
    }

    override suspend fun save(accountProjection: AccountProjection): Option<Id<Account>> {
        return collection.insertOne(accountProjection).toOption()
            .mapNotNull { it.insertedId }
            .map { it.asObjectId().value.toId() }
    }
}