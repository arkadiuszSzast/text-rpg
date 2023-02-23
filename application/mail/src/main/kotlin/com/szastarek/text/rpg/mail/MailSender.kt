package com.szastarek.text.rpg.mail

interface MailSender {
    suspend fun send(mail: MailAggregate): MailSentResult
}
