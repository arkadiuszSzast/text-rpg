package com.szastarek.text.rpg.mail.command

import com.szastarek.acl.CanDoAnythingAuthorizedAccountAbilityProvider
import com.szastarek.acl.DenyAllAuthorizedAccountAbilityProvider
import com.szastarek.acl.authority.AuthorizedAccountAbilityProvider
import com.szastarek.text.rpg.event.store.EventStoreTest
import com.szastarek.text.rpg.mail.MailSender
import com.szastarek.text.rpg.mail.MailSendingError
import com.szastarek.text.rpg.mail.MailSentResult
import com.szastarek.text.rpg.mail.RecordingMailSender
import com.szastarek.text.rpg.mail.event.MailSentSuccessfullyEvent
import com.szastarek.text.rpg.mail.mailModule
import com.szastarek.text.rpg.security.config.JwtAuthConfig
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.test.utils.faker
import io.kotest.core.spec.style.DescribeSpec
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.inject
import org.litote.kmongo.coroutine.CoroutineDatabase
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue

private val testingModules = module {
    single<MailSender> {
        RecordingMailSender {
            when (it.to) {
                EmailAddress.create("invalid@mail.com") ->
                    MailSentResult.Error(it.id.cast(), MailSendingError("Invalid mail address"))

                else -> MailSentResult.Success(it.id.cast())
            }
        }
    }
    single<AuthorizedAccountAbilityProvider>(named("canDoAnythingAbilityProvider")) { CanDoAnythingAuthorizedAccountAbilityProvider() }
    single<AuthorizedAccountAbilityProvider>(named("denyAllAbilityProvider")) { DenyAllAuthorizedAccountAbilityProvider() }
    single(named("sendSuccessfullyMailCommandHandler")) { SendMailCommandHandler(get(), get(), get()) }
    single(named("sendingErrorMailCommandHandler")) { SendMailCommandHandler(get(), get(), get()) }
}

class SendMailCommandHandlerTest : EventStoreTest(testingModules) {

    init {

        val sendSuccessfullyMailCommandHandler by inject<SendMailCommandHandler>(named("sendSuccessfullyMailCommandHandler"))
        val sendingErrorMailCommandHandler by inject<SendMailCommandHandler>(named("sendingErrorMailCommandHandler"))
        val mailSender by inject<RecordingMailSender>()

        describe("SendMailCommandHandler") {

            it("should send mail") {
                val command = faker.mailModule.sendMailCommand()

                val result = sendSuccessfullyMailCommandHandler.handleAsync(command)
                val mailSentEvents = eventStoreDb.readAllByEventType(MailSentSuccessfullyEvent.eventType)

                expectThat(result).isA<MailSentResult.Success>()

                expectThat(mailSender.hasBeenSentSuccessfully(result)).isTrue()
                expectThat(mailSentEvents) {
                    hasSize(1)
                    get { first().event.eventType == MailSentSuccessfullyEvent.fullEventType.get() }.isTrue()
                }
            }

            it("when sending failed should save MailSendingErrorEvent") {
                withKoin(mainModules + handlerModule) {
                    val handler = get<SendMailCommandHandler>()
                    val mailSender = get<RecordingMailSender>()
                    val eventStore = get<RecordingEventStoreDB>()

                    val mail = faker.mailModule.mailDto().copy(to = EmailAddress.create("invalid@mail.com"))

                    val result = handler.handleAsync(SendMailCommand(mail))
                    val mailSentEvents =
                        eventStore.readStream(StreamName("${mailAggregateType.type}-${mail.id}")).events

                    expectThat(result).isEqualTo(
                        MailSentResult.Error(
                            mail.id,
                            MailSendingError("Invalid mail address")
                        )
                    )
                    expectThat(mailSender.hasNotBeenSentSuccessfully(mail)).isTrue()
                    expectThat(mailSentEvents) {
                        hasSize(1)
                        get { first().event.eventType == MailSendingErrorEvent.fullEventType.get() }.isTrue()
                    }
                }
            }

            it("should save MailSendingErrorEvent when have no permissions") {
                withKoin(mainModules + denyAllHandlerModule) {
                    val handler = get<SendMailCommandHandler>()
                    val mailSender = get<RecordingMailSender>()
                    val eventStore = get<RecordingEventStoreDB>()

                    val mail = faker.mailModule.mailDto()

                    val result = handler.handleAsync(SendMailCommand(mail))
                    val mailSentEvents =
                        eventStore.readStream(StreamName("${mailAggregateType.type}-${mail.id}")).events

                    expectThat(result).isEqualTo(
                        MailSentResult.Error(
                            mail.id,
                            MailSendingError("Mail send failed because of lack of permissions")
                        )
                    )
                    expectThat(mailSender.getAll().isEmpty()).isTrue()
                    expectThat(mailSentEvents) {
                        hasSize(1)
                        get { first().event.eventType == MailSendingErrorEvent.fullEventType.get() }.isTrue()
                    }
                }
            }
        }
    }
}
