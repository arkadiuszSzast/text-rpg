package com.szastarek.text.rpg.account

import com.szastarek.acl.authority.Authority
import com.szastarek.text.rpg.account.event.AccountCreatedEvent
import com.szastarek.text.rpg.test.utils.faker
import org.litote.kmongo.newId
import pl.brightinventions.codified.enums.codifiedEnum

class AccountCreatedEventBuilder {
    var accountId = newId<Account>()
    var emailAddress = faker.accountModule.emailAddress()
    var status = faker.random.nextEnum<AccountStatus>()
    var roleName = faker.accountModule.roleName()
    var customAuthorities = emptyList<Authority>()
    var password = faker.accountModule.hashedPassword()
    var createdAt = faker.accountModule.createdAt()
    var timeZone = faker.accountModule.timeZone()

    fun build() =
        AccountCreatedEvent(
            accountId,
            emailAddress,
            status.codifiedEnum(),
            password,
            roleName,
            customAuthorities,
            timeZone,
            createdAt,
        )
}
