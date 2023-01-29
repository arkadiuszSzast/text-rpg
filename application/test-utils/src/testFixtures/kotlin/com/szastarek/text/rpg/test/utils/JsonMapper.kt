package com.szastarek.text.rpg.test.utils

import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule

object JsonMapper {
    val defaultMapper = Json {
        encodeDefaults = true
        prettyPrint = true
        ignoreUnknownKeys = true
        serializersModule = IdKotlinXSerializationModule
    }
}
