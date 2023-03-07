package com.szastarek.text.rpg.account

import com.szastarek.text.rpg.HasDatabaseAndTransactionally

suspend fun HasDatabaseAndTransactionally.createAccountAggregate(customize: AccountAggregateBuilder.() -> Unit = {}): AccountAggregate {
    val accountAggregate = AccountAggregateBuilder().apply(customize).build()
    db.getCollection<AccountAggregate>().insertOne(accountAggregate)
    return accountAggregate
}
