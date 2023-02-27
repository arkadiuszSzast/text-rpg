package com.szastarek.text.rpg.mail

import org.litote.kmongo.Id

class RecordingMailSender(
    private val mailMapper: (MailAggregate) -> MailSentResult = ::defaultRecordingMailSenderMapper
) : MailSender {
    private val sentMails: MutableMap<Id<Mail>, MailSentResult> = mutableMapOf()

    override suspend fun send(mail: MailAggregate): MailSentResult {
        val result = mailMapper(mail)
        sentMails[mail.id] = result

        return result
    }

    fun getAll() = sentMails.toMap()
    fun mailNotSent(mailId: Id<Mail>) = sentMails[mailId] == null
    fun hasBeenSentSuccessfully(mailId: Id<Mail>) = sentMails[mailId] == MailSentResult.Success(mailId)
    fun hasNotBeenSentSuccessfully(mailId: Id<Mail>) = sentMails[mailId] is MailSentResult.Error
    fun clear() = sentMails.clear()
}

private fun defaultRecordingMailSenderMapper(mail: MailAggregate): MailSentResult {
    return MailSentResult.Success(mail.id)
}
