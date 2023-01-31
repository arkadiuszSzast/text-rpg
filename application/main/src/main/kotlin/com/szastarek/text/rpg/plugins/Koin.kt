package com.szastarek.text.rpg.plugins

import com.szastarek.text.rpg.json.jsonModule
import com.szastarek.text.rpg.mongoModule
import io.ktor.server.application.Application
import org.koin.ktor.plugin.koin

internal fun Application.configureKoin() {
    koin {
        modules(
            jsonModule,
            mongoModule,
        )
    }
}
