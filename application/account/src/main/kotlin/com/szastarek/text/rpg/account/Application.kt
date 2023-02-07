package com.szastarek.text.rpg.account

import io.ktor.server.application.Application


@Suppress("unused")
fun Application.accountModule() {
    configureAccountRouting()
    configureEventStoreSubscribers()
}
