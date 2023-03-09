package com.szastarek.text.rpg.account

import com.szastarek.text.rpg.account.event.AccountCreatedEvent

fun AccountAggregate.Companion.apply(event: AccountCreatedEvent): AccountAggregate {
    return AccountAggregate(
        event.accountId,
        event.emailAddress,
        event.status,
        event.roleName,
        event.customAuthorities,
        event.hashedPassword,
        event.createdAt,
        event.timeZone
    )
}
