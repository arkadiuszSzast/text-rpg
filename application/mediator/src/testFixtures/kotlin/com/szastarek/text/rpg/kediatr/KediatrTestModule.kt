package com.szastarek.text.rpg.kediatr

import com.trendyol.kediatr.koin.KediatRKoin
import org.koin.dsl.module

val kediatrTestModule = module {
    single { KediatRKoin.getMediator("com.szastarek.text.rpg") }
}
