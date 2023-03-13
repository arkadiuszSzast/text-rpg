package com.szastarek.text.rpg.mail.command

import com.szastarek.acl.CanDoAnythingAuthorizedAccountAbilityProvider
import com.szastarek.acl.DenyAllAuthorizedAccountAbilityProvider
import com.szastarek.event.store.db.InMemoryEventStoreDB
import com.szastarek.text.rpg.event.store.getAs
import com.szastarek.text.rpg.mail.MailSendingError
import com.szastarek.text.rpg.mail.MailSentResult
import com.szastarek.text.rpg.mail.RecordingMailSender
import com.szastarek.text.rpg.mail.event.MailSendingErrorEvent
import com.szastarek.text.rpg.mail.event.MailSentSuccessfullyEvent
import com.szastarek.text.rpg.mail.mailModule
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.test.utils.faker
import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.arrow.isRight
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue

class SendMailCommandHandlerTest : DescribeSpec() {

    private val eventStoreDb = InMemoryEventStoreDB()
    private val mailSender = RecordingMailSender {
        when (it.to) {
            EmailAddress.create("invalid@mail.com") ->
                MailSentResult.Error(it.id.cast(), MailSendingError("Invalid mail address"))

            else -> MailSentResult.Success(it.id.cast())
        }
    }
    private val allowAllSendMailHandler = SendMailCommandHandler(mailSender, eventStoreDb, CanDoAnythingAuthorizedAccountAbilityProvider())
    private val denyAllSendMailHandler = SendMailCommandHandler(mailSender, eventStoreDb, DenyAllAuthorizedAccountAbilityProvider())

    init {

        beforeEach {
            eventStoreDb.clear()
            mailSender.clear()
        }

        describe("SendMailCommandHandler") {

            it("should send mail") {
                //arrange
                val command = faker.mailModule.sendMailCommand()

                //act
                val result = allowAllSendMailHandler.handle(command)

                //assert
                expectThat(result).isA<MailSentResult.Success>()
                expectThat(mailSender.hasBeenSentSuccessfully(result.mailId)).isTrue()

                val mailSentEvents = eventStoreDb.readAllByEventType(MailSentSuccessfullyEvent.eventType).events
                expectThat(mailSentEvents) {
                    hasSize(1)
                    get { first().event.getAs<MailSentSuccessfullyEvent>() }
                        .isRight()
                        .get { value.mailId }.isEqualTo(result.mailId)
                }
            }

            it("when sending failed should save MailSendingErrorEvent") {
                //arrange
                val command = faker.mailModule.sendMailCommand(to = EmailAddress.create("invalid@mail.com"))

                //act
                val result = allowAllSendMailHandler.handle(command)

                //assert
                expectThat(result).isA<MailSentResult.Error>()
                expectThat(mailSender.hasNotBeenSentSuccessfully(result.mailId)).isTrue()

                val mailSentEvents = eventStoreDb.readAllByEventType(MailSendingErrorEvent.eventType).events
                expectThat(mailSentEvents) {
                    hasSize(1)
                    get { first().event.getAs<MailSentSuccessfullyEvent>() }
                        .isRight()
                        .get { value.mailId }.isEqualTo(result.mailId)
                }
            }

            it("should save MailSendingErrorEvent when have no permissions") {
                //arrange
                val command = faker.mailModule.sendMailCommand()

                //act
                val result = denyAllSendMailHandler.handle(command)

                //assert
                expectThat(result).isA<MailSentResult.Error>()
                expectThat(mailSender.mailNotSent(result.mailId)).isTrue()

                val mailSentEvents = eventStoreDb.readAllByEventType(MailSendingErrorEvent.eventType).events
                expectThat(mailSentEvents) {
                    hasSize(1)
                    get { first().event.getAs<MailSentSuccessfullyEvent>() }
                        .isRight()
                        .get { value.mailId }.isEqualTo(result.mailId)
                }
            }
        }
    }
}
