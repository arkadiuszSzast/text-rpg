package com.szastarek.text.rpg.security

import com.auth0.jwt.exceptions.JWTDecodeException
import com.szastarek.acl.AccountId
import com.szastarek.text.rpg.shared.EmailAddress
import io.ktor.server.auth.jwt.JWTPrincipal

val JWTPrincipal.accountId
    get() = this["accountId"]?.let { AccountId(it) } ?: throw JWTDecodeException("Claim accountId is missing")

val JWTPrincipal.role
    get() = this["role"]?.let { RoleNames.find(it) } ?: throw JWTDecodeException("Claim role is missing")

val JWTPrincipal.email
    get() = this["email"]?.let { EmailAddress.create(it) } ?: throw JWTDecodeException("Claim email is missing")
