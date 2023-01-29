package com.szastarek.text.rpg.json

import com.szastarek.text.rpg.shared.json.JsonMapper
import org.koin.dsl.module

val jsonModule = module {
    single { JsonMapper.defaultMapper }
}
