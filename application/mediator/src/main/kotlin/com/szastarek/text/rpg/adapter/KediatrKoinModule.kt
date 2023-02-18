package com.szastarek.text.rpg.adapter

import com.szastarek.text.rpg.AsyncProcessingPipelineBehavior
import com.szastarek.text.rpg.ProcessingPipelineBehavior
import com.trendyol.kediatr.koin.KediatrKoin
import org.koin.dsl.module

val kediatrKoinModule = module {
    single { KediatrKoin.getCommandBus() }
    single { ProcessingPipelineBehavior(emptyList()) }
    single { AsyncProcessingPipelineBehavior(emptyList()) }
}
