package com.szastarek.text.rpg.shared.validation.rules

import com.auth0.jwt.JWT
import com.szastarek.text.rpg.shared.validation.specialCharacters
import io.konform.validation.Constraint
import io.konform.validation.ValidationBuilder

fun ValidationBuilder<String>.containsNumber(): Constraint<String> = addConstraint(
    "must contains a number"
) {
    val numbersPattern = """([0-9])"""
    numbersPattern.toRegex().containsMatchIn(it)
}

fun ValidationBuilder<String>.containsSpecialCharacter(): Constraint<String> =
    addConstraint(
        "must contains one of special characters [$specialCharacters]"
    ) {
        val specialCharactersPattern = """([${specialCharacters.joinToString(",")}])"""
        specialCharactersPattern.toRegex().containsMatchIn(it)
    }

fun ValidationBuilder<String>.cannotHaveWhitespaces(): Constraint<String> = addConstraint(
    "cannot contains whitespace character"
) {
    val whitespacesPattern = """\s"""
    whitespacesPattern.toRegex().containsMatchIn(it).not()
}

fun ValidationBuilder<String>.isValidJwt(): Constraint<String> = addConstraint(
    "not a valid JWT token"
) {
    runCatching { JWT.decode(it) }.isSuccess
}
