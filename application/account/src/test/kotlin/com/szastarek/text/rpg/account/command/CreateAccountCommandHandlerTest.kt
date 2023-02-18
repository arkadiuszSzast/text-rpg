package com.szastarek.text.rpg.account.command

import com.eventstore.dbclient.StreamNotFoundException
import com.szastarek.text.rpg.account.AccountAggregateRepository
import com.szastarek.text.rpg.account.accountModule
import com.szastarek.text.rpg.account.adapter.AccountAggregateMongoRepository
import com.szastarek.text.rpg.account.createAccountAggregate
import com.szastarek.text.rpg.account.event.AccountCreatedEvent
import com.szastarek.text.rpg.account.support.DatabaseAndEventStoreTest
import com.szastarek.text.rpg.event.store.getAs
import com.szastarek.text.rpg.security.RoleNames
import com.szastarek.text.rpg.shared.validation.ValidationException
import com.szastarek.text.rpg.test.utils.faker
import com.szastarek.text.rpg.test.utils.isUpToOneSecondOld
import kotlinx.coroutines.runBlocking
import org.awaitility.kotlin.await
import org.awaitility.kotlin.ignoreException
import org.awaitility.kotlin.untilAsserted
import org.koin.core.component.get
import org.koin.dsl.bind
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineDatabase
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.arrow.isRight
import strikt.assertions.first
import strikt.assertions.hasSize
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo

private val testingModules = module {
    single { AccountAggregateMongoRepository(get<CoroutineDatabase>().getCollection()) } bind AccountAggregateRepository::class
    single { CreateAccountCommandHandler(get(), get()) }
}

class CreateAccountCommandHandlerTest : DatabaseAndEventStoreTest(testingModules) {

    private val handler = get<CreateAccountCommandHandler>()

    init {

        describe("CreateAccountCommandHandler") {

            it("should create account") {
                //arrange
                val command = faker.accountModule.createAccountCommand()
                val handler = get<CreateAccountCommandHandler>()

                //act
                val result = handler.handleAsync(command)

                //assert
                await ignoreException StreamNotFoundException::class untilAsserted {
                    runBlocking {
                        expectThat(eventStoreDb.readAllByEventType(AccountCreatedEvent.eventType).events) {
                            hasSize(1)
                            first().get { event.getAs<AccountCreatedEvent>() }
                                .isRight()
                                .get { value }
                                .and { get { accountId }.isEqualTo(result.accountId.cast()) }
                                .and { get { emailAddress }.isEqualTo(command.emailAddress) }
                                .and { get { timeZone }.isEqualTo(command.timeZone) }
                                .and { get { createdAt.value }.isUpToOneSecondOld() }
                                .and { get { roleName }.isEqualTo(RoleNames.regularUser) }
                                .and { get { customAuthorities }.isEmpty() }
                        }
                    }
                }
            }

            it("should not create account when email is already taken") {
                // arrange
                val command = faker.accountModule.createAccountCommand()
                createAccountAggregate { emailAddress = command.emailAddress }

                // act && assert
                expectThrows<ValidationException> {
                    handler.handleAsync(command)
                }
                expectThrows<StreamNotFoundException> {
                    eventStoreDb.readAllByEventType(AccountCreatedEvent.eventType)
                }
            }
        }

    }

}
