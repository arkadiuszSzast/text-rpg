package com.szastarek.text.rpg.account.adapter

import arrow.core.Option
import arrow.core.toOption
import com.szastarek.text.rpg.account.Account
import com.szastarek.text.rpg.account.AccountAggregate
import com.szastarek.text.rpg.account.AccountAggregateRepository
import com.szastarek.text.rpg.shared.EmailAddress
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.id.toId

internal class AccountAggregateMongoRepository(
    private val collection: CoroutineCollection<AccountAggregate>
) : AccountAggregateRepository {
    override suspend fun findById(id: Id<Account>): Option<AccountAggregate> {
        return collection.findOneById(id).toOption()
    }

    override suspend fun existsByEmail(email: EmailAddress): Boolean {
        return findByEmail(email).isDefined()
    }

    override suspend fun findByEmail(email: EmailAddress): Option<AccountAggregate> {
        return collection.findOne(AccountAggregate::emailAddress eq email).toOption()
    }

    override suspend fun save(accountAggregate: AccountAggregate): Option<Id<Account>> {
        return collection.insertOne(accountAggregate).toOption()
            .mapNotNull { it.insertedId }
            .map { it.asObjectId().value.toId() }
    }
}
