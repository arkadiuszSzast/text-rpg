package com.szastarek.text.rpg.security

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class RoleName internal constructor(val value: String)
