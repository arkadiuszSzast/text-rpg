package com.szastarek.text.rpg.account.subscriber

import com.szastarek.text.rpg.account.AccountAggregateRepository
import com.szastarek.text.rpg.account.AccountStatus
import com.szastarek.text.rpg.account.accountModule
import com.szastarek.text.rpg.account.adapter.AccountAggregateMongoRepository
import com.szastarek.text.rpg.account.support.DatabaseAndEventStoreTest
import com.szastarek.text.rpg.event.store.appendToStream
import com.szastarek.text.rpg.test.utils.faker
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.koin.core.component.get
import org.koin.dsl.bind
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineDatabase
import pl.brightinventions.codified.enums.codifiedEnum
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
                    application {
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

            it("should activate account on AccountActivatedEvent") {
                testApplication {
                    application {
                        accountAggregateUpdater(eventStoreDb, get())

                        launch {
                            //arrange
                            val accountAggregate = faker.accountModule.accountAggregate { status = AccountStatus.Staged }
                                    .also { accountAggregateRepository.save(it) }
                            val accountActivatedEvent = faker.accountModule.accountActivatedEvent(accountAggregate.id)

                            //act
                            eventStoreDb.appendToStream(accountActivatedEvent)

                            //assert
                            val accountProjection = accountAggregateRepository.findById(accountActivatedEvent.accountId)

                            expectThat(accountProjection) {
                                isSome()
                                    .get { value }
                                    .isEqualTo(accountAggregate.copy(status = AccountStatus.Active.codifiedEnum()))
                            }
                        }
                    }
                }
            }
        }
    }
}
