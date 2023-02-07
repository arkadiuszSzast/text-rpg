package com.szastarek.text.rpg

import com.szastarek.text.rpg.plugins.configureExceptionsHandling
import com.szastarek.text.rpg.plugins.configureKoin
import io.ktor.server.application.Application
import org.koin.ktor.ext.get

fun main(args: Array<String>) =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.main() {
    configureKoin()
    configureSecurity()
    configureSerialization(get())
    configureRouting()
    configureExceptionsHandling()
}
