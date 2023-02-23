package com.szastarek.text.rpg.mail.command

import com.szastarek.text.rpg.mail.MailSentResult
import com.szastarek.text.rpg.mail.MailSubject
import com.szastarek.text.rpg.mail.MailTemplateId
import com.szastarek.text.rpg.mail.MailVariables
import com.szastarek.text.rpg.shared.EmailAddress
import com.trendyol.kediatr.CommandMetadata
import com.trendyol.kediatr.CommandWithResult

data class SendMailCommand(
    val subject: MailSubject,
    val from: EmailAddress,
    val to: EmailAddress,
    val templateId: MailTemplateId,
    val variables: MailVariables,
    override val metadata: CommandMetadata? = null
) : CommandWithResult<MailSentResult>
