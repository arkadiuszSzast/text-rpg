package com.szastarek.text.rpg.account.command

import com.eventstore.dbclient.StreamNotFoundException
import com.szastarek.event.store.db.InMemoryEventStoreDB
import com.szastarek.text.rpg.account.InMemoryAccountAggregateRepository
import com.szastarek.text.rpg.account.accountModule
import com.szastarek.text.rpg.account.event.AccountCreatedEvent
import com.szastarek.text.rpg.event.store.getAs
import com.szastarek.text.rpg.security.RoleNames
import com.szastarek.text.rpg.shared.validation.ValidationException
import com.szastarek.text.rpg.test.utils.faker
import com.szastarek.text.rpg.test.utils.isUpToOneSecondOld
import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.arrow.isRight
import strikt.assertions.first
import strikt.assertions.hasSize
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo

class CreateAccountCommandHandlerTest : DescribeSpec() {

    private val eventStoreDb = InMemoryEventStoreDB()
    private val accountAggregateRepository = InMemoryAccountAggregateRepository()
    private val handler = CreateAccountCommandHandler(accountAggregateRepository, eventStoreDb)

    init {

        beforeEach {
            eventStoreDb.clear()
            accountAggregateRepository.clear()
        }

        describe("CreateAccountCommandHandler") {

            it("should create account") {
                //arrange
                val command = faker.accountModule.createAccountCommand()

                //act
                val result = handler.handleAsync(command)

                //assert
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

            it("should not create account when email is already taken") {
                // arrange
                val command = faker.accountModule.createAccountCommand()
                accountAggregateRepository.save(
                    faker.accountModule.accountAggregate { emailAddress = command.emailAddress }
                )

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
