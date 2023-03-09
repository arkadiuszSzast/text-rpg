package com.szastarek.text.rpg.shared.config

object ApplicationConfig {
    val environment by lazy { getProperty(Keys.environment) }
    val webClientAppUrl by lazy { getProperty(Keys.webClientAppUrl) }

    private object Keys {
        val environment = ConfigKey("application.env")
        val webClientAppUrl = ConfigKey("application.webClientAppUrl")
    }
}
