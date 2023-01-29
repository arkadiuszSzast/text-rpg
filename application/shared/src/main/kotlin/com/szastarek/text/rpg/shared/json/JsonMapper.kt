package com.szastarek.text.rpg.shared.json

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
