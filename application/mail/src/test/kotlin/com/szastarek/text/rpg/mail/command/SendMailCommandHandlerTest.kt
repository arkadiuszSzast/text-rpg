package com.szastarek.text.rpg.mail.command

import com.szastarek.acl.CanDoAnythingAuthorizedAccountAbilityProvider
import com.szastarek.acl.DenyAllAuthorizedAccountAbilityProvider
import com.szastarek.acl.authority.AuthorizedAccountAbilityProvider
import com.szastarek.text.rpg.event.store.EventStoreTest
import com.szastarek.text.rpg.event.store.getAs
import com.szastarek.text.rpg.mail.MailSender
import com.szastarek.text.rpg.mail.MailSendingError
import com.szastarek.text.rpg.mail.MailSentResult
import com.szastarek.text.rpg.mail.RecordingMailSender
import com.szastarek.text.rpg.mail.event.MailSendingErrorEvent
import com.szastarek.text.rpg.mail.event.MailSentSuccessfullyEvent
import com.szastarek.text.rpg.mail.mailModule
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.test.utils.faker
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.inject
import strikt.api.expectThat
import strikt.arrow.isRight
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue

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
    single<AuthorizedAccountAbilityProvider>(named("canDoAnythingAbilityProvider")) { CanDoAnythingAuthorizedAccountAbilityProvider() }
    single<AuthorizedAccountAbilityProvider>(named("denyAllAbilityProvider")) { DenyAllAuthorizedAccountAbilityProvider() }
    single(named("allowAllSendMailCommandHandler")) { SendMailCommandHandler(get(), get(), get(named("canDoAnythingAbilityProvider"))) }
    single(named("denyAllSendMailCommandHandler")) { SendMailCommandHandler(get(), get(), get(named("denyAllAbilityProvider"))) }
}

class SendMailCommandHandlerTest : EventStoreTest(testingModules) {

    init {

        val allowAllSendMailHandler by inject<SendMailCommandHandler>(named("allowAllSendMailCommandHandler"))
        val denyAllSendMailHandler by inject<SendMailCommandHandler>(named("denyAllSendMailCommandHandler"))
        val mailSender by inject<RecordingMailSender>()

        describe("SendMailCommandHandler") {

            it("should send mail") {
                //given
                val command = faker.mailModule.sendMailCommand()

                //when
                val result = allowAllSendMailHandler.handleAsync(command)

                //then
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
                //given
                val command = faker.mailModule.sendMailCommand(to = EmailAddress.create("invalid@mail.com"))

                //when
                val result = allowAllSendMailHandler.handleAsync(command)

                //then
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
                //given
                val command = faker.mailModule.sendMailCommand()

                //when
                val result = denyAllSendMailHandler.handleAsync(command)

                //then
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
