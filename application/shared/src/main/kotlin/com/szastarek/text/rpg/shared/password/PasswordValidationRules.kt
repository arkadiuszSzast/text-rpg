package com.szastarek.text.rpg.shared.password

import com.szastarek.text.rpg.shared.validation.rules.cannotHaveWhitespaces
import com.szastarek.text.rpg.shared.validation.rules.containsNumber
import com.szastarek.text.rpg.shared.validation.rules.containsSpecialCharacter
import io.konform.validation.Validation
import io.konform.validation.jsonschema.minLength

private const val DEFAULT_MIN_PASSWORD_LENGTH = 12

val defaultPasswordValidator = Validation<String> {
    minLength(DEFAULT_MIN_PASSWORD_LENGTH) hint "validation.password_too_short"
    containsNumber() hint ("validation.password_must_contains_number")
    containsSpecialCharacter() hint ("validation.password_must_contains_special_character")
    cannotHaveWhitespaces() hint ("validation.password_cannot_have_whitespaces")
}
