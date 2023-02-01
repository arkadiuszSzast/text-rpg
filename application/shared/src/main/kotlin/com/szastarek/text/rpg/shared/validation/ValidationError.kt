package com.szastarek.text.rpg.shared.validation

import io.konform.validation.ValidationError

data class ValidationError(override val dataPath: String, override val message: String) : ValidationError
