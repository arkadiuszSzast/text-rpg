package com.szastarek.text.rpg

import com.szastarek.text.rpg.plugins.configureExceptionsHandling
import com.szastarek.text.rpg.plugins.configureKoin
import com.szastarek.text.rpg.plugins.configureSecurity
import com.szastarek.text.rpg.plugins.configureSerialization
import com.szastarek.text.rpg.security.config.JwtAuthConfig
import io.ktor.server.application.*
import io.ktor.server.application.Application
import org.koin.ktor.ext.get

fun main(args: Array<String>) =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.main() {
    configureKoin()
    configureSecurity(JwtAuthConfig)
    configureSerialization(get())
    configureRouting()
    configureExceptionsHandling()
}
