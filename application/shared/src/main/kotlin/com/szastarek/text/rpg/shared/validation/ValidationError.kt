package com.szastarek.text.rpg.shared.validation

import io.konform.validation.ValidationError
import kotlinx.serialization.Serializable

@Serializable
data class ValidationError(override val dataPath: String, override val message: String) : ValidationError
