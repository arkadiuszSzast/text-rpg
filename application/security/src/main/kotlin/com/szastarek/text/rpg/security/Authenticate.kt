package com.szastarek.text.rpg.security

import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route

fun Route.authenticate(
    vararg configurations: String? = arrayOf(null),
    optional: Boolean = false,
    build: Route.() -> Unit
): Route {
    return authenticate(*configurations, optional = optional) {
        install(AuthenticatedAccountContextPlugin)
        build()
    }
}