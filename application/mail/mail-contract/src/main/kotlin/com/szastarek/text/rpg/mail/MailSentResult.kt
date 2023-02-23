package com.szastarek.text.rpg.mail

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

sealed class MailSentResult {
    @Serializable
    data class Success(@Contextual val mailId: Id<Mail>) : MailSentResult()

    @Serializable
    data class Error(@Contextual val mailId: Id<Mail>, val cause: MailSendingError) : MailSentResult()
}
