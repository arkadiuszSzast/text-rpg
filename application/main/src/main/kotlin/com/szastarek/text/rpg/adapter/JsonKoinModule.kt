package com.szastarek.text.rpg.adapter

import com.szastarek.text.rpg.shared.json.JsonMapper
import org.koin.dsl.module

val jsonKoinModule = module {
    single { JsonMapper.defaultMapper }
}
