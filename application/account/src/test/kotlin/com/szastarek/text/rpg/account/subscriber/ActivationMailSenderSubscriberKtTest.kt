package com.szastarek.text.rpg.account.subscriber

import com.szastarek.acl.CanDoAnythingAuthorizedAccountAbilityProvider
import com.szastarek.acl.authority.AuthorizedAccountAbilityProvider
import com.szastarek.text.rpg.account.accountModule
import com.szastarek.text.rpg.account.activation.command.GenerateAccountActivationLinkCommandHandler
import com.szastarek.text.rpg.account.activation.event.AccountActivationMailSentEvent
import com.szastarek.text.rpg.account.config.JwtConfig
import com.szastarek.text.rpg.account.config.MailConfig
import com.szastarek.text.rpg.account.support.DatabaseAndEventStoreTest
import com.szastarek.text.rpg.adapter.kediatrKoinModule
import com.szastarek.text.rpg.event.store.appendToStream
import com.szastarek.text.rpg.event.store.getAs
import com.szastarek.text.rpg.mail.FakeSendMailCommandHandler
import com.szastarek.text.rpg.mail.MailSender
import com.szastarek.text.rpg.mail.MailSendingError
import com.szastarek.text.rpg.mail.MailSentResult
import com.szastarek.text.rpg.mail.RecordingMailSender
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.shared.config.ApplicationConfig
import com.szastarek.text.rpg.test.utils.faker
import com.trendyol.kediatr.Mediator
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.dsl.bind
import org.koin.dsl.module
import strikt.api.expectThat
import strikt.arrow.isRight
import strikt.assertions.first
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo

private val testingModules = module {
   single {
      RecordingMailSender {
         when (it.to) {
            EmailAddress.create("invalid@mail.com") ->
               MailSentResult.Error(it.id.cast(), MailSendingError("Invalid mail address"))

            else -> MailSentResult.Success(it.id.cast())
         }
      }
   } bind MailSender::class
   single { CanDoAnythingAuthorizedAccountAbilityProvider() } bind AuthorizedAccountAbilityProvider::class
   single { GenerateAccountActivationLinkCommandHandler(ApplicationConfig, JwtConfig, get()) }
   single { FakeSendMailCommandHandler(get(), get(), get()) }
}.plus(kediatrKoinModule)
class ActivationMailSenderSubscriberKtTest : DatabaseAndEventStoreTest(testingModules) {

   private val mediator = get<Mediator>()
   private val mailSender = get<RecordingMailSender>()

   init {

      describe("ActivationMailSenderSubscriber") {

         it("should send activation email") {
            testApplication {
               this.application {
                  this.activationMailSenderSubscriber(eventStoreDb, mediator, MailConfig)

                  launch {
                     // arrange
                     val accountAggregateCreatedEvent = faker.accountModule.accountCreatedEvent()

                     // act
                     eventStoreDb.appendToStream(accountAggregateCreatedEvent)

                     // assert
                     val sendingMailsResults = mailSender.getAll().values
                     expectThat(sendingMailsResults) {
                        hasSize(1)
                        and { first().isA<MailSentResult.Success>() }
                     }
                     val accountActivationMailSentEvents = eventStoreDb.readAllByEventType(
                        AccountActivationMailSentEvent.eventType
                     ).events.map { it.event }

                     expectThat(accountActivationMailSentEvents) {
                        hasSize(1)
                        first().get { this.getAs<AccountActivationMailSentEvent>() }
                           .isRight().get { this.value }
                           .get { this.accountId }.isEqualTo(accountAggregateCreatedEvent.accountId)
                     }
                  }
               }
            }
         }

         it("should not send activation email") {
            testApplication {
               this.application {
                  this.activationMailSenderSubscriber(eventStoreDb, mediator, MailConfig)

                  launch {
                     // arrange
                     val accountAggregateCreatedEvent = faker.accountModule.accountCreatedEvent {
                        emailAddress = EmailAddress.create("invalid@mail.com")
                     }

                     // act
                     eventStoreDb.appendToStream(accountAggregateCreatedEvent)

                     // assert
                     val sendingMailsResults = mailSender.getAll().values
                     expectThat(sendingMailsResults) {
                        hasSize(1)
                        and { first().isA<MailSentResult.Error>() }
                     }
                     val accountActivationMailSentEvents = eventStoreDb.readAllByEventType(
                        AccountActivationMailSentEvent.eventType
                     ).events.map { it.event }

                     expectThat(accountActivationMailSentEvents).isEmpty()
                  }
               }
            }
         }
      }

   }
}
