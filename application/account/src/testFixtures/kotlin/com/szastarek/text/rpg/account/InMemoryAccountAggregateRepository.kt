package com.szastarek.text.rpg.account

import arrow.core.Option
import arrow.core.toOption
import com.szastarek.text.rpg.shared.EmailAddress
import java.util.concurrent.ConcurrentHashMap
import org.litote.kmongo.Id

class InMemoryAccountAggregateRepository : AccountAggregateRepository {
    private val db: MutableMap<Id<Account>, AccountAggregate> = ConcurrentHashMap()

    fun clear() {
        db.clear()
    }
    override suspend fun findById(id: Id<Account>): Option<AccountAggregate> {
        return db[id].toOption()
    }

    override suspend fun existsByEmail(email: EmailAddress): Boolean {
        return findByEmail(email).isDefined()
    }

    override suspend fun findByEmail(email: EmailAddress): Option<AccountAggregate> {
        return db.values.find { it.emailAddress == email }.toOption()
    }

    override suspend fun save(accountAggregate: AccountAggregate): Option<Id<Account>> {
        db[accountAggregate.id] = accountAggregate
        return accountAggregate.id.toOption()
    }
}
