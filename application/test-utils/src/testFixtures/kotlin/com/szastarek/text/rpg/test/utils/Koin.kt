package com.szastarek.text.rpg.test.utils

import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module

suspend fun <R> withKoin(module: Module, action: suspend Koin.() -> R) {
    try {
        val koin = startKoin {
            modules(module)
        }.koin
        action(koin)
    } finally {
        stopKoin()
    }
}

suspend fun <R> withKoin(modules: List<Module>, action: suspend Koin.() -> R) {
    try {
        val koin = startKoin {
            modules(modules)
        }.koin
        action(koin)
    } finally {
        stopKoin()
    }
}
