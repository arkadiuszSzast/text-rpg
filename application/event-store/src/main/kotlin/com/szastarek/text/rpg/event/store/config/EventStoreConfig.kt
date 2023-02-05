package com.szastarek.text.rpg.event.store.config

import com.szastarek.text.rpg.shared.config.ConfigKey
import com.szastarek.text.rpg.shared.config.getProperty

internal object EventStoreConfig {

    val connectionString by lazy { getProperty(Keys.connectionString) }

    private object Keys {
        val connectionString = ConfigKey("eventStore.connectionString")
    }
}
