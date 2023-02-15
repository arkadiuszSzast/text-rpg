package com.szastarek.text.rpg.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.szastarek.text.rpg.security.config.JwtAuthConfig
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureSecurity(jwtConfig: JwtAuthConfig) {

    install(Authentication) {
        jwt {
            realm = jwtConfig.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtConfig.secret))
                    .withAudience(jwtConfig.audience)
                    .withIssuer(jwtConfig.issuer)
                    .build()
            )
            validate { credential ->
                when (credential.payload.audience.contains(jwtConfig.audience)) {
                    true -> {
                        JWTPrincipal(credential.payload)
                    }

                    else -> null
                }
            }
        }
    }

}
