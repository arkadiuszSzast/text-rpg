package com.szastarek.text.rpg.shared.jwt

import kotlinx.serialization.KSerializer
import pl.brightinventions.codified.Codified
import pl.brightinventions.codified.enums.CodifiedEnum
import pl.brightinventions.codified.enums.serializer.codifiedEnumSerializer

enum class JwtValidationError(override val code: String) : Codified<String> {
    AlgorithmMismatch("algorithm_mismatch"),
    InvalidSignature("invalid_signature"),
    Expired("expired"),
    InvalidClaim("invalid_claim");

    object CodifiedSerializer : KSerializer<CodifiedEnum<JwtValidationError, String>> by codifiedEnumSerializer()
}
