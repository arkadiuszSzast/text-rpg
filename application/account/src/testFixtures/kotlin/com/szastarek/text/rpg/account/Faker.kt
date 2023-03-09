package com.szastarek.text.rpg.account

import com.szastarek.text.rpg.account.command.CreateAccountCommand
import com.szastarek.text.rpg.account.request.CreateAccountRequest
import com.szastarek.text.rpg.security.RoleNames
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.shared.date.CreatedDate
import com.szastarek.text.rpg.shared.password.RawPassword
import com.szastarek.text.rpg.test.utils.date
import io.github.serpro69.kfaker.Faker
import java.util.concurrent.TimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

val Faker.accountModule: AccountModule
    get() = AccountModule(this)

class AccountModule(private val faker: Faker) {
    fun emailAddress() = EmailAddress.create("${faker.pokemon.names()}@mail.com")

    fun roleName() = faker.random.randomValue(listOf(RoleNames.regularUser, RoleNames.superUser))

    fun rawPassword() =
        RawPassword(faker.random.randomString(10) + faker.random.nextChar().uppercase() + faker.random.nextInt() + "#")

    fun hashedPassword() = rawPassword().hashpw()

    fun timeZone() = TimeZone.of(faker.address.timeZone())

    fun lastLoggedInDate(atMost: Long = 10, unit: TimeUnit = TimeUnit.DAYS) =
        LastLoggedInDate(faker.date.localDateTime.past(atMost, unit).toInstant(TimeZone.UTC))

    fun createdAt(atMost: Long = 10, unit: TimeUnit = TimeUnit.DAYS) =
        CreatedDate(faker.date.localDateTime.past(atMost, unit).toInstant(TimeZone.UTC))

    fun createAccountRequest() = CreateAccountRequest(emailAddress(), rawPassword(), timeZone())

    fun createAccountCommand() = CreateAccountCommand(emailAddress(), rawPassword(), timeZone())

    fun accountAggregate(customize: AccountAggregateBuilder.() -> Unit = {}) =
        AccountAggregateBuilder().apply(customize).build()

    fun accountCreatedEvent(customize: AccountCreatedEventBuilder.() -> Unit = {}) =
        AccountCreatedEventBuilder().apply(customize).build()
}
