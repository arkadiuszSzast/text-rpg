package com.szastarek.text.rpg.mail.config

import com.szastarek.text.rpg.shared.config.ConfigKey
import com.szastarek.text.rpg.shared.config.getProperty

internal object SendGridConfig {
    val apiKey by lazy { getProperty(Keys.apiKey) }

    private object Keys {
        val apiKey = ConfigKey("mail.sendgridApiKey")
    }
}
