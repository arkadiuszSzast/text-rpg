package com.szastarek.text.rpg.test.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.szastarek.text.rpg.shared.jwt.JwtToken
import java.time.Instant
import java.util.Date

fun jwt(customize: JwtBuilder.() -> Unit): String {
    return JwtBuilder().apply(customize).build()
}

fun jwtToken(customize: JwtBuilder.() -> Unit): JwtToken {
    return JwtToken.createOrThrow(JwtBuilder().apply(customize).build())
}

class JwtBuilder {
    var subject: String? = null
    var expirationDate: Instant? = null
    var secret: String = "secret"

    fun build(): String {
        val jwt = JWT.create()
        subject?.let { jwt.withSubject(subject) }
        expirationDate?.let { jwt.withExpiresAt(Date.from(it)) }

        return jwt.sign(Algorithm.HMAC256(secret))
    }
}
