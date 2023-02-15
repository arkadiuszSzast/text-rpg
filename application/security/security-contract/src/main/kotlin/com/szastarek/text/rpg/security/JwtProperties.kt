package com.szastarek.text.rpg.security

data class JwtProperties(val secret: JwtSecret, val issuer: JwtIssuer, val expirationTime: JwtExpirationTime)

@JvmInline
value class JwtSecret(val value: String)

@JvmInline
value class JwtIssuer(val value: String)

@JvmInline
value class JwtExpirationTime(val millis: Long)
