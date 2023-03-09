package com.szastarek.text.rpg.account.config

import com.szastarek.text.rpg.security.JwtExpirationTime
import com.szastarek.text.rpg.security.JwtIssuer
import com.szastarek.text.rpg.security.JwtProperties
import com.szastarek.text.rpg.security.JwtSecret
import com.szastarek.text.rpg.shared.config.ConfigKey
import com.szastarek.text.rpg.shared.config.getProperty

internal object JwtConfig {
    val activateAccount by lazy {
        val secret = getProperty(Keys.activateAccount + Keys.secret)
        val issuer = getProperty(Keys.activateAccount + Keys.issuer)
        val expirationInMillis = getProperty(Keys.activateAccount + Keys.expirationInMillis).toLong()

        JwtProperties(
            JwtSecret(secret),
            JwtIssuer(issuer),
            JwtExpirationTime(expirationInMillis)
        )
    }

    private object Keys {
        val secret = ConfigKey("secret")
        val issuer = ConfigKey("issuer")
        val expirationInMillis = ConfigKey("expirationInMillis")
        val activateAccount = ConfigKey("jwt.activateAccount")
    }
}
