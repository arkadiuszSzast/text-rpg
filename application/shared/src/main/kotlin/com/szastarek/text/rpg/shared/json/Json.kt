package com.szastarek.text.rpg.shared.json

import arrow.core.Either
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

inline fun <reified T> Json.decodeFromString(content: String) =
    Either.catch { decodeFromString<T>(content) }

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> Json.decodeFromStream(content: ByteArray) =
    Either.catch { decodeFromStream<T>(content.inputStream()) }
