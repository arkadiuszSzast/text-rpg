package com.szastarek.text.rpg.account

import com.szastarek.acl.authority.Authority
import com.szastarek.text.rpg.test.utils.faker
import org.litote.kmongo.newId
import pl.brightinventions.codified.enums.codifiedEnum

class AccountAggregateBuilder {
    var id = newId<Account>()
    var emailAddress = faker.accountModule.emailAddress()
    var status = faker.random.nextEnum<AccountStatus>()
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
            status = status.codifiedEnum(),
            roleName = roleName,
            customAuthorities = customAuthorities,
            password = password,
            createdAt = createdAt,
            timeZone = timeZone,
            lastLoggedInDate = lastLoggedInDate,
        )
}
