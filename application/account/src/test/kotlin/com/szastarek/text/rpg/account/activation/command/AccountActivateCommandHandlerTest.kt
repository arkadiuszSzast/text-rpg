package com.szastarek.text.rpg.account.activation.command

import com.szastarek.event.store.db.InMemoryEventStoreDB
import com.szastarek.event.store.db.StreamName
import com.szastarek.text.rpg.account.Account
import com.szastarek.text.rpg.account.AccountStatus
import com.szastarek.text.rpg.account.InMemoryAccountAggregateRepository
import com.szastarek.text.rpg.account.accountModule
import com.szastarek.text.rpg.account.activation.AccountActivationError
import com.szastarek.text.rpg.account.activation.event.AccountActivatedEvent
import com.szastarek.text.rpg.account.activation.event.AccountActivationFailureEvent
import com.szastarek.text.rpg.account.config.JwtConfig
import com.szastarek.text.rpg.event.store.getAs
import com.szastarek.text.rpg.shared.jwt.TokenMissingSubjectException
import com.szastarek.text.rpg.test.utils.faker
import com.szastarek.text.rpg.test.utils.jwtToken
import io.kotest.core.spec.style.DescribeSpec
import java.time.Instant
import org.litote.kmongo.newId
import pl.brightinventions.codified.enums.codifiedEnum
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.arrow.isRight
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class AccountActivateCommandHandlerTest : DescribeSpec() {

    private val eventStoreDb = InMemoryEventStoreDB()
    private val accountAggregateRepository = InMemoryAccountAggregateRepository()
    private val handler = AccountActivateCommandHandler(JwtConfig, eventStoreDb, accountAggregateRepository)

    init {

        beforeEach {
            eventStoreDb.clear()
            accountAggregateRepository.clear()
        }

        describe("AccountActivateCommandHandler") {

            it("throw exception when no subject claim") {
                // arrange
                val token = jwtToken {
                    secret = JwtConfig.activateAccount.secret.value
                }
                val command = ActivateAccountCommand(token)

                // act && assert
                expectThrows<TokenMissingSubjectException> {
                    handler.handleAsync(command)
                }
            }

            it("save activation failed event when token signature is invalid") {
                // arrange
                val accountId = newId<Account>()
                val token = jwtToken { subject = accountId.toString(); secret = "invalid" }
                val command = ActivateAccountCommand(token)

                // act
                val result = handler.handleAsync(command)

                // assert
                val streamName = StreamName("${AccountActivationFailureEvent.eventCategory.value}-$accountId")
                val events = eventStoreDb.readStream(streamName).events

                expectThat(result)
                    .isA<ActivateAccountCommandFailure>()
                    .and { get { this.errorCode }.isEqualTo(AccountActivationError.TokenInvalid.codifiedEnum()) }
                    .and { get { this.accountId }.isEqualTo(accountId) }

                expectThat(events)
                    .hasSize(1)
                    .get { first().originalEvent.getAs<AccountActivationFailureEvent>() }
                    .isRight()
                    .get { value }
                    .and { get { this.accountId }.isEqualTo(accountId) }
                    .and { get { this.reason }.isEqualTo(AccountActivationError.TokenInvalid.codifiedEnum()) }
            }

            it("save activation failed event when token expired") {
                // arrange
                val accountId = newId<Account>()
                val token = jwtToken {
                    subject = accountId.toString()
                    expirationDate = Instant.now().minusSeconds(10)
                    secret = JwtConfig.activateAccount.secret.value
                }
                val command = ActivateAccountCommand(token)

                // act
                val result = handler.handleAsync(command)

                // assert
                val streamName = StreamName("${AccountActivationFailureEvent.eventCategory.value}-$accountId")
                val events = eventStoreDb.readStream(streamName).events

                expectThat(result)
                    .isA<ActivateAccountCommandFailure>()
                    .and { get { this.errorCode }.isEqualTo(AccountActivationError.TokenExpired.codifiedEnum()) }
                    .and { get { this.accountId }.isEqualTo(accountId) }

                expectThat(events)
                    .hasSize(1)
                    .get { first().originalEvent.getAs<AccountActivationFailureEvent>() }
                    .isRight()
                    .get { value }
                    .and { get { this.accountId }.isEqualTo(accountId) }
                    .and { get { this.reason }.isEqualTo(AccountActivationError.TokenExpired.codifiedEnum()) }
            }

            it("save activation failed event when account not found") {
                // arrange
                val accountId = newId<Account>()
                val token = jwtToken {
                    subject = accountId.toString()
                    expirationDate = Instant.now().plusSeconds(10)
                    secret = JwtConfig.activateAccount.secret.value
                }
                val command = ActivateAccountCommand(token)

                // act
                val result = handler.handleAsync(command)

                // assert
                val streamName = StreamName("${AccountActivationFailureEvent.eventCategory.value}-$accountId")
                val events = eventStoreDb.readStream(streamName).events

                expectThat(result)
                    .isA<ActivateAccountCommandFailure>()
                    .and { get { this.errorCode }.isEqualTo(AccountActivationError.AccountNotFound.codifiedEnum()) }
                    .and { get { this.accountId }.isEqualTo(accountId) }

                expectThat(events)
                    .hasSize(1)
                    .get { first().originalEvent.getAs<AccountActivationFailureEvent>() }
                    .isRight()
                    .get { value }
                    .and { get { this.accountId }.isEqualTo(accountId) }
                    .and { get { this.reason }.isEqualTo(AccountActivationError.AccountNotFound.codifiedEnum()) }
            }

            it("save activation failed event when account is in suspended status") {
                // arrange
                val account = faker.accountModule.accountAggregate { status = AccountStatus.Suspended }
                    .also { accountAggregateRepository.save(it) }
                val accountId = account.id
                val token = jwtToken {
                    subject = accountId.toString()
                    expirationDate = Instant.now().plusSeconds(10)
                    secret = JwtConfig.activateAccount.secret.value
                }
                val command = ActivateAccountCommand(token)

                // act
                val result = handler.handleAsync(command)

                // assert
                val streamName = StreamName("${AccountActivationFailureEvent.eventCategory.value}-$accountId")
                val events = eventStoreDb.readStream(streamName).events

                expectThat(result)
                    .isA<ActivateAccountCommandFailure>()
                    .and { get { this.errorCode }.isEqualTo(AccountActivationError.AccountSuspended.codifiedEnum()) }
                    .and { get { this.accountId }.isEqualTo(accountId) }

                expectThat(events)
                    .hasSize(1)
                    .get { first().originalEvent.getAs<AccountActivationFailureEvent>() }
                    .isRight()
                    .get { value }
                    .and { get { this.accountId }.isEqualTo(accountId) }
                    .and { get { this.reason }.isEqualTo(AccountActivationError.AccountSuspended.codifiedEnum()) }
            }

            it("save activation failed event when account is in active status") {
                // arrange
                val account = faker.accountModule.accountAggregate { status = AccountStatus.Active }
                    .also { accountAggregateRepository.save(it) }
                val accountId = account.id
                val token = jwtToken {
                    subject = accountId.toString()
                    expirationDate = Instant.now().plusSeconds(10)
                    secret = JwtConfig.activateAccount.secret.value
                }
                val command = ActivateAccountCommand(token)

                // act
                val result = handler.handleAsync(command)

                // assert
                val streamName = StreamName("${AccountActivationFailureEvent.eventCategory.value}-$accountId")
                val events = eventStoreDb.readStream(streamName).events

                expectThat(result)
                    .isA<ActivateAccountCommandFailure>()
                    .and { get { errorCode }.isEqualTo(AccountActivationError.AccountActive.codifiedEnum()) }
                    .and { get { accountId }.isEqualTo(accountId) }

                expectThat(events)
                    .hasSize(1)
                    .get { first().originalEvent.getAs<AccountActivationFailureEvent>() }
                    .isRight()
                    .get { value }
                    .and { get { this.accountId }.isEqualTo(accountId) }
                    .and { get { this.reason }.isEqualTo(AccountActivationError.AccountActive.codifiedEnum()) }
            }


            it("should activate account") {
                // arrange
                val account = faker.accountModule.accountAggregate { status = AccountStatus.Staged }
                    .also { accountAggregateRepository.save(it) }
                val accountId = account.id
                val token = jwtToken {
                    subject = accountId.toString()
                    expirationDate = Instant.now().plusSeconds(10)
                    secret = JwtConfig.activateAccount.secret.value
                }
                val command = ActivateAccountCommand(token)

                // act
                val result = handler.handleAsync(command)

                // assert
                val streamName = StreamName("${AccountActivatedEvent.eventCategory.value}-$accountId")
                val events = eventStoreDb.readStream(streamName).events

                expectThat(result)
                    .isA<ActivateAccountCommandSucceed>()
                    .get { this.accountId }.isEqualTo(accountId)

                expectThat(events)
                    .hasSize(1)
                    .get { first().originalEvent.getAs<AccountActivatedEvent>() }
                    .isRight()
                    .get { value }
                    .and { get { this.accountId }.isEqualTo(accountId) }
            }
        }
    }
}
