package com.szastarek.text.rpg.mail

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

sealed interface MailSentResult {
    val mailId: Id<Mail>

    @Serializable
    data class Success(@Contextual override val mailId: Id<Mail>) : MailSentResult

    @Serializable
    data class Error(@Contextual override val mailId: Id<Mail>, val cause: MailSendingError) : MailSentResult
}
