package com.szastarek.text.rpg.account.command

import com.eventstore.dbclient.StreamNotFoundException
import com.szastarek.text.rpg.account.AccountAggregateRepository
import com.szastarek.text.rpg.account.LogInFailureError
import com.szastarek.text.rpg.account.accountModule
import com.szastarek.text.rpg.account.adapter.AccountAggregateMongoRepository
import com.szastarek.text.rpg.account.createAccountAggregate
import com.szastarek.text.rpg.account.event.AccountLoggedInFailureEvent
import com.szastarek.text.rpg.account.event.AccountLoggedInSuccessEvent
import com.szastarek.text.rpg.account.support.DatabaseAndEventStoreTest
import com.szastarek.text.rpg.event.store.getAs
import com.szastarek.text.rpg.security.config.JwtAuthConfig
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.shared.password.RawPassword
import com.szastarek.text.rpg.test.utils.faker
import kotlinx.coroutines.runBlocking
import org.awaitility.kotlin.await
import org.awaitility.kotlin.ignoreException
import org.awaitility.kotlin.untilAsserted
import org.koin.core.component.get
import org.koin.dsl.bind
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineDatabase
import strikt.api.expectThat
import strikt.arrow.isRight
import strikt.assertions.first
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEqualTo

private val testingModules = module {
    single { AccountAggregateMongoRepository(get<CoroutineDatabase>().getCollection()) } bind AccountAggregateRepository::class
    single { LoginAccountHandler(JwtAuthConfig, get(), get()) }
}

class LoginAccountHandlerTest : DatabaseAndEventStoreTest(testingModules) {
    private val handler = get<LoginAccountHandler>()

    init {
        describe("LoginAccountHandler") {

            it("should login account") {
                //arrange
                val rawPassword = faker.accountModule.rawPassword()
                val account = createAccountAggregate { password = rawPassword.hashpw() }
                val command = LoginAccountCommand(account.emailAddress, rawPassword)

                //act
                val result = handler.handleAsync(command)

                //assert
                expectThat(result) {
                    isA<LoginAccountCommandSucceed>()
                        .get { email }.isEqualTo(account.emailAddress)
                }
                await ignoreException StreamNotFoundException::class untilAsserted {
                    runBlocking {
                        expectThat(eventStoreDb.readAllByEventType(AccountLoggedInSuccessEvent.eventType).events) {
                            hasSize(1)
                            first().get { event.getAs<AccountLoggedInSuccessEvent>() }
                                .isRight()
                                .get { value }
                                .and { get { accountId }.isEqualTo(account.id) }
                        }
                    }
                }
            }

            it("should not login account when invalid password given") {
                //arrange
                val rawPassword = faker.accountModule.rawPassword()
                val account = createAccountAggregate { password = rawPassword.hashpw() }
                val command = LoginAccountCommand(account.emailAddress, RawPassword("invalid password"))

                //act
                val result = handler.handleAsync(command)

                //assert
                expectThat(result) {
                    isA<LoginAccountCommandFailure>()
                        .and { get { email }.isEqualTo(account.emailAddress) }
                        .and { get { errorCode.code() }.isEqualTo(LogInFailureError.InvalidPassword.code) }
                }
                await ignoreException StreamNotFoundException::class untilAsserted {
                    runBlocking {
                        expectThat(eventStoreDb.readAllByEventType(AccountLoggedInFailureEvent.eventType).events) {
                            hasSize(1)
                            first().get { event.getAs<AccountLoggedInFailureEvent>() }
                                .isRight()
                                .get { value }
                                .and { get { accountId }.isEqualTo(account.id) }
                                .and { get { error.code() }.isEqualTo(LogInFailureError.InvalidPassword.code) }
                        }
                    }
                }
            }

            it("should not login account when account not found") {
                //arrange
                val emailAddress = EmailAddress.create("not_existing@mail.com")
                val command = LoginAccountCommand(emailAddress, RawPassword("some_password"))

                //act
                val result = handler.handleAsync(command)

                //assert
                expectThat(result) {
                    isA<LoginAccountCommandFailure>()
                        .and { get { email }.isEqualTo(emailAddress) }
                        .and { get { errorCode.code() }.isEqualTo(LogInFailureError.AccountNotFound.code) }
                }
            }
        }
    }
}
