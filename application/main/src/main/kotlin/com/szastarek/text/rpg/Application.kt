package com.szastarek.text.rpg

import com.szastarek.text.rpg.plugins.configureKoin
import io.ktor.server.application.Application

fun main(args: Array<String>) =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.main() {
    configureKoin()
    configureSecurity()
    configureSerialization()
    configureRouting()
}
