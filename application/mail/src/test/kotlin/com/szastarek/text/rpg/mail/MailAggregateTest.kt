package com.szastarek.text.rpg.mail

import com.szastarek.text.rpg.mail.event.MailSendingErrorEvent
import com.szastarek.text.rpg.mail.event.MailSentSuccessfullyEvent
import com.szastarek.text.rpg.shared.EmailAddress
import com.szastarek.text.rpg.test.utils.faker
import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.assertions.isA

class MailAggregateTest : DescribeSpec() {

    private val mailSender = RecordingMailSender {
        when (it.to) {
            EmailAddress.create("invalid@mail.com") ->
                MailSentResult.Error(it.id.cast(), MailSendingError("Invalid mail address"))
            else -> MailSentResult.Success(it.id.cast())
        }
    }

    init {

        beforeEach {
            mailSender.clear()
        }

        describe("send mail") {

            it("should return mail MailSentSuccessfullyEvent") {
                // arrange
                val mail = faker.mailModule.mailAggregate()

                // act
                val result = mail.send(mailSender)

                // assert
                expectThat(result) {
                    isA<MailSentSuccessfullyEvent>()
                }
            }

            it("should return mail MailSendingErrorEvent") {
                val mail = faker.mailModule.mailAggregate(to = EmailAddress.create("invalid@mail.com"))

                val result = mail.send(mailSender)

                expectThat(result) {
                    isA<MailSendingErrorEvent>()
                }
            }
        }
    }
}
