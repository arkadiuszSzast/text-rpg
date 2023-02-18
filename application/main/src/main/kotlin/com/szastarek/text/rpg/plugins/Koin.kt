package com.szastarek.text.rpg.plugins

import com.szastarek.text.rpg.account.adapter.accountKoinModule
import com.szastarek.text.rpg.event.store.plugins.eventStoreDbKoinModule
import com.szastarek.text.rpg.adapter.jsonKoinModule
import com.szastarek.text.rpg.adapter.kediatrKoinModule
import com.szastarek.text.rpg.adapter.mongoKoinModule
import com.szastarek.text.rpg.security.adapter.securityKoinModule
import io.ktor.server.application.Application
import org.koin.ktor.plugin.koin

internal fun Application.configureKoin() {
    koin {
        modules(
            jsonKoinModule,
            mongoKoinModule,
            kediatrKoinModule,
            eventStoreDbKoinModule,
            accountKoinModule,
            securityKoinModule
        )
    }
}
