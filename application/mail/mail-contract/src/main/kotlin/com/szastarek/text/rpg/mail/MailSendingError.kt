package com.szastarek.text.rpg.mail

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class MailSendingError(val value: String)
