package com.szastarek.text.rpg.account.subscriber

import com.szastarek.text.rpg.account.AccountAggregateRepository
import com.szastarek.text.rpg.account.accountModule
import com.szastarek.text.rpg.account.adapter.AccountAggregateMongoRepository
import com.szastarek.text.rpg.account.support.DatabaseAndEventStoreTest
import com.szastarek.text.rpg.event.store.appendToStream
import com.szastarek.text.rpg.test.utils.faker
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.dsl.bind
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineDatabase
import strikt.api.expectThat
import strikt.arrow.isSome
import strikt.assertions.isEqualTo

private val testingModules = module {
    single { AccountAggregateMongoRepository(get<CoroutineDatabase>().getCollection()) } bind AccountAggregateRepository::class
    single { AccountAggregateUpdater(get()) }
}

class AccountAggregateSubscriberKtTest : DatabaseAndEventStoreTest(testingModules) {

    private val accountAggregateRepository = get<AccountAggregateRepository>()

    init {
        describe("AccountAggregateSubscriber") {
            it("should create account projection on AccountCreatedEvent") {
                testApplication {
                    this.application {
                        accountAggregateUpdater(eventStoreDb, get())

                        launch {
                            // arrange
                            val accountCreatedEvent = faker.accountModule.accountCreatedEvent()

                            // act
                            eventStoreDb.appendToStream(accountCreatedEvent)

                            // assert
                            val accountProjection = accountAggregateRepository.findById(accountCreatedEvent.accountId)

                            expectThat(accountProjection) {
                                isSome()
                                    .get { value }
                                    .and { get { emailAddress }.isEqualTo(accountCreatedEvent.emailAddress) }
                                    .and { get { status }.isEqualTo(accountCreatedEvent.status) }
                                    .and { get { password }.isEqualTo(accountCreatedEvent.hashedPassword) }
                                    .and { get { roleName }.isEqualTo(accountCreatedEvent.roleName) }
                                    .and { get { customAuthorities }.isEqualTo(accountCreatedEvent.customAuthorities) }
                                    .and { get { timeZone }.isEqualTo(accountCreatedEvent.timeZone) }
                                    .and { get { createdAt }.isEqualTo(accountCreatedEvent.createdAt) }
                            }
                        }
                    }
                }
            }
        }
    }
}
