package com.szastarek.text.rpg.adapter

import com.trendyol.kediatr.koin.KediatRKoin
import org.koin.dsl.module

val kediatrKoinModule = module {
    single { KediatRKoin.getMediator() }
}
