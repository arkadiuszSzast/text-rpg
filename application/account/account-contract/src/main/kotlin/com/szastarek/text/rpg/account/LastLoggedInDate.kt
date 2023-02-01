package com.szastarek.text.rpg.account

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class LastLoggedInDate(val value: Instant)
