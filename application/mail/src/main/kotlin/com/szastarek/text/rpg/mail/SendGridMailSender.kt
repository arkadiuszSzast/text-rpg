package com.szastarek.text.rpg.mail

import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.objects.Email
import com.sendgrid.helpers.mail.objects.Personalization
import com.szastarek.text.rpg.shared.response.ResponseCode
import com.sendgrid.helpers.mail.Mail as SendGridMail

internal class SendGridMailSender(private val sendGridClient: SendGrid) : MailSender {
    override suspend fun send(mail: MailAggregate): MailSentResult {
        val sendGridMail = mail.toSendgridMail()

        val request = Request().apply {
            method = Method.POST
            endpoint = "mail/send"
            body = sendGridMail.build()
        }

        val response = sendGridClient.api(request)
        val statusCode = ResponseCode(response.statusCode)

        return when {
            statusCode.isSuccess -> MailSentResult.Success(mail.id)
            else -> MailSentResult.Error(mail.id, MailSendingError(response.body))
        }
    }

    private fun MailAggregate.toSendgridMail(): SendGridMail {
        val mail = this
        val personalization = Personalization().apply {
            addTo(Email(mail.to.value))
            mail.variables.variables.forEach { addDynamicTemplateData(it.key, it.value) }
        }

        return SendGridMail().apply {
            from = Email(mail.from.value)
            templateId = mail.templateId.id
            addPersonalization(personalization)
        }
    }
}
