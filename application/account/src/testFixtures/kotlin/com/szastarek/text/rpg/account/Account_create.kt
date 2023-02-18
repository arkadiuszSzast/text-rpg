package com.szastarek.text.rpg.account

import com.szastarek.acl.authority.Authority
import com.szastarek.text.rpg.HasDatabaseAndTransactionally
import com.szastarek.text.rpg.test.utils.faker
import org.litote.kmongo.newId


suspend fun HasDatabaseAndTransactionally.createAccountAggregate(customize: AccountAggregateBuilder.() -> Unit = {}): AccountAggregate {
    val accountAggregate = AccountAggregateBuilder().apply(customize).build()
    db.getCollection<AccountAggregate>().insertOne(accountAggregate)
    return accountAggregate
}

class AccountAggregateBuilder {
    var id = newId<Account>()
    var emailAddress = faker.accountModule.emailAddress()
    var roleName = faker.accountModule.roleName()
    var customAuthorities = emptyList<Authority>()
    var password = faker.accountModule.hashedPassword()
    var createdAt = faker.accountModule.createdAt()
    var timeZone = faker.accountModule.timeZone()
    var lastLoggedInDate = faker.accountModule.lastLoggedInDate()

    fun build() =
        AccountAggregate(
            id = id,
            emailAddress = emailAddress,
            roleName = roleName,
            customAuthorities = customAuthorities,
            password = password,
            createdAt = createdAt,
            timeZone = timeZone,
            lastLoggedInDate = lastLoggedInDate,
        )
}