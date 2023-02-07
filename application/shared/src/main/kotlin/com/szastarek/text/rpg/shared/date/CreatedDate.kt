package com.szastarek.text.rpg.shared.date

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class CreatedDate(val value: Instant)
