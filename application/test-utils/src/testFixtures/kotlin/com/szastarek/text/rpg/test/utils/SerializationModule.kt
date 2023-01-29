package com.szastarek.text.rpg.test.utils

import com.szastarek.text.rpg.test.utils.JsonMapper.defaultMapper
import org.koin.dsl.module

val serializationModule = module {
    single { defaultMapper }
}
