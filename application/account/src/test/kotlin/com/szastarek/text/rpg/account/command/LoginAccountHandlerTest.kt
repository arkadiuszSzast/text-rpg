package com.szastarek.text.rpg.account.command

import com.szastarek.event.store.db.InMemoryEventStoreDB
import com.szastarek.text.rpg.account.AccountStatus
import com.szastarek.text.rpg.account.InMemoryAccountAggregateRepository
import com.szastarek.text.rpg.account.LogInFailureError
import com.szastarek.text.rpg.account.accountModule
import com.szastarek.text.rpg.account.event.AccountLoggedInFailureEvent
import com.szastarek.text.rpg.account.event.AccountLoggedInSuccessEvent
import com.szastarek.text.rpg.event.store.getAs
import com.szastarek.text.rpg.security.config.JwtAuthConfig
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.shared.password.RawPassword
import com.szastarek.text.rpg.test.utils.faker
import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.arrow.isRight
import strikt.assertions.first
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class LoginAccountHandlerTest : DescribeSpec() {

    private val eventStoreDb = InMemoryEventStoreDB()
    private val accountAggregateRepository = InMemoryAccountAggregateRepository()
    private val handler = LoginAccountHandler(JwtAuthConfig, accountAggregateRepository, eventStoreDb)

    init {

        beforeEach {
            eventStoreDb.clear()
            accountAggregateRepository.clear()
        }

        describe("LoginAccountHandler") {

            it("should login account") {
                //arrange
                val rawPassword = faker.accountModule.rawPassword()
                val account = faker.accountModule.accountAggregate {
                    status = AccountStatus.Active
                    password = rawPassword.hashpw()
                }.also { accountAggregateRepository.save(it) }
                val command = LoginAccountCommand(account.emailAddress, rawPassword)

                //act
                val result = handler.handle(command)

                //assert
                expectThat(result) {
                    isA<LoginAccountCommandSucceed>()
                        .get { email }.isEqualTo(account.emailAddress)
                }
                expectThat(eventStoreDb.readAllByEventType(AccountLoggedInSuccessEvent.eventType).events) {
                    hasSize(1)
                    first().get { event.getAs<AccountLoggedInSuccessEvent>() }
                        .isRight()
                        .get { value }
                        .and { get { accountId }.isEqualTo(account.id) }
                }
            }
        }

        it("should not login account when invalid password given") {
            //arrange
            val rawPassword = faker.accountModule.rawPassword()
            val account = faker.accountModule.accountAggregate { password = rawPassword.hashpw() }
                .also { accountAggregateRepository.save(it) }
            val command = LoginAccountCommand(account.emailAddress, RawPassword("invalid password"))

            //act
            val result = handler.handle(command)

            //assert
            expectThat(result) {
                isA<LoginAccountCommandFailure>()
                    .and { get { email }.isEqualTo(account.emailAddress) }
                    .and { get { errorCode.code() }.isEqualTo(LogInFailureError.InvalidPassword.code) }
            }
            expectThat(eventStoreDb.readAllByEventType(AccountLoggedInFailureEvent.eventType).events) {
                hasSize(1)
                first().get { event.getAs<AccountLoggedInFailureEvent>() }
                    .isRight()
                    .get { value }
                    .and { get { accountId }.isEqualTo(account.id) }
                    .and { get { reason.code() }.isEqualTo(LogInFailureError.InvalidPassword.code) }
            }
        }

        it("should not login account when account is suspended") {
            //arrange
            val rawPassword = faker.accountModule.rawPassword()
            val account = faker.accountModule.accountAggregate {
                status = AccountStatus.Suspended
                password = rawPassword.hashpw()
            }.also { accountAggregateRepository.save(it) }
            val command = LoginAccountCommand(account.emailAddress, rawPassword)

            //act
            val result = handler.handle(command)

            //assert
            expectThat(result) {
                isA<LoginAccountCommandFailure>()
                    .and { get { email }.isEqualTo(account.emailAddress) }
                    .and { get { errorCode.code() }.isEqualTo(LogInFailureError.AccountSuspended.code) }
            }
            expectThat(eventStoreDb.readAllByEventType(AccountLoggedInFailureEvent.eventType).events) {
                hasSize(1)
                first().get { event.getAs<AccountLoggedInFailureEvent>() }
                    .isRight()
                    .get { value }
                    .and { get { accountId }.isEqualTo(account.id) }
                    .and { get { reason.code() }.isEqualTo(LogInFailureError.AccountSuspended.code) }
            }
        }

        it("should not login account when account is inactive") {
            //arrange
            val rawPassword = faker.accountModule.rawPassword()
            val account = faker.accountModule.accountAggregate {
                status = AccountStatus.Staged
                password = rawPassword.hashpw()
            }.also { accountAggregateRepository.save(it) }
            val command = LoginAccountCommand(account.emailAddress, rawPassword)

            //act
            val result = handler.handle(command)

            //assert
            expectThat(result) {
                isA<LoginAccountCommandFailure>()
                    .and { get { email }.isEqualTo(account.emailAddress) }
                    .and { get { errorCode.code() }.isEqualTo(LogInFailureError.AccountNotActivated.code) }
            }
            expectThat(eventStoreDb.readAllByEventType(AccountLoggedInFailureEvent.eventType).events) {
                hasSize(1)
                first().get { event.getAs<AccountLoggedInFailureEvent>() }
                    .isRight()
                    .get { value }
                    .and { get { accountId }.isEqualTo(account.id) }
                    .and { get { reason.code() }.isEqualTo(LogInFailureError.AccountNotActivated.code) }
            }
        }

        it("should not login account when account not found") {
            //arrange
            val emailAddress = EmailAddress.create("not_existing@mail.com")
            val command = LoginAccountCommand(emailAddress, RawPassword("some_password"))

            //act
            val result = handler.handle(command)

            //assert
            expectThat(result) {
                isA<LoginAccountCommandFailure>()
                    .and { get { email }.isEqualTo(emailAddress) }
                    .and { get { errorCode.code() }.isEqualTo(LogInFailureError.AccountNotFound.code) }
            }
        }
    }
}
