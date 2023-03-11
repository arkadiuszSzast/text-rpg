package com.szastarek.text.rpg.account

import arrow.core.Option
import com.szastarek.text.rpg.shared.EmailAddress
import org.litote.kmongo.Id

interface AccountAggregateRepository {
    suspend fun findById(id: Id<Account>): Option<AccountAggregate>
    suspend fun existsByEmail(email: EmailAddress): Boolean
    suspend fun findByEmail(email: EmailAddress): Option<AccountAggregate>
    suspend fun save(accountAggregate: AccountAggregate): Option<Id<Account>>
    suspend fun updateById(id: Id<Account>, value: AccountAggregate): Option<Id<Account>>
}
