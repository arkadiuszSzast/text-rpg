package com.szastarek.text.rpg

import com.trendyol.kediatr.koin.KediatrKoin
import org.koin.dsl.module

val kediatrModule = module {
    single { KediatrKoin.getCommandBus() }
    single { ProcessingPipelineBehavior(emptyList()) }
    single { AsyncProcessingPipelineBehavior(emptyList()) }
}
