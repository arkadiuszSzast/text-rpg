package com.szastarek.text.rpg.shared.jwt

import arrow.core.Invalid
import arrow.core.Valid
import arrow.core.Validated
import arrow.core.invalid
import arrow.core.valid
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.AlgorithmMismatchException
import com.auth0.jwt.exceptions.InvalidClaimException
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.szastarek.text.rpg.shared.validation.Validatable
import com.szastarek.text.rpg.shared.validation.rules.isValidJwt
import io.konform.validation.Validation
import kotlinx.serialization.Serializable
import pl.brightinventions.codified.enums.codifiedEnum

@JvmInline
@Serializable
value class JwtToken private constructor(val token: String) : Validatable<JwtToken> {
    companion object {
        fun create(token: String): Validated<JWTDecodeException, JwtToken> {
            return try {
                JWT.decode(token).token.let { JwtToken(it) }.valid()
            } catch (e: JWTDecodeException) {
                e.invalid()
            }
        }

        fun createOrThrow(token: String): JwtToken =
            when (val vToken = create(token)) {
                is Invalid -> throw vToken.value
                is Valid -> vToken.value
            }

        val validator = Validation {
            JwtToken::token {
                isValidJwt() hint "validation.invalid_jwt_token"
            }
        }
    }

    fun verify(algorithm: Algorithm) = Validated.catch { JWT.require(algorithm).build().verify(token) }
        .mapLeft {
            when (it) {
                is AlgorithmMismatchException -> JwtValidationError.AlgorithmMismatch.codifiedEnum()
                is SignatureVerificationException -> JwtValidationError.InvalidSignature.codifiedEnum()
                is TokenExpiredException -> JwtValidationError.Expired.codifiedEnum()
                is InvalidClaimException -> JwtValidationError.InvalidClaim.codifiedEnum()
                else -> "unhandled_token_error".codifiedEnum()
            }
        }

    override val validator: Validation<JwtToken>
        get() = Companion.validator
}
